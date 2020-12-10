package org.aksw.limes.spark;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class SparkHR3Mapper {

    private transient final JavaSparkContext sc = JavaSparkContext.fromSparkContext(SparkSession.active().sparkContext());

    private static StructType inputType() {
        return new StructType()
                .add("blockId", DataTypes.LongType, false)
                .add("polygon", DataTypes.createArrayType(DataTypes.DoubleType, false), false)
                .add("url", DataTypes.StringType, false)
                .add("boundingCircle", DataTypes.createArrayType(DataTypes.DoubleType, false));
    }

    private static final ExpressionEncoder<Row> inputEncoder =  RowEncoder.apply(inputType());
    private static final ExpressionEncoder<Row> joinEncoder = RowEncoder.apply(
            inputType().add("targetBlockId", DataTypes.LongType, false)
    );
    private static final ExpressionEncoder<Row> blockMapEncoder = RowEncoder.apply(
            new StructType()
                    .add("sourceBlockId", DataTypes.LongType, false)
                    .add("targetBlockId", DataTypes.LongType, false)
    );
    private static final ExpressionEncoder<Row> outputEncoder = RowEncoder.apply(
            new StructType()
                    .add("source", DataTypes.StringType, false)
                    .add("target", DataTypes.StringType, false)
                    .add("distance", DataTypes.DoubleType, false)
    );

    public Dataset<Row> getMapping(Dataset<Row> source, Dataset<Row> target, double threshold, int granularity) {
        final double distanceThreshold = (1 / threshold) - 1; // [0,1] -> ℝ (in km)
        final double angularThreshold = Math.toDegrees(distanceThreshold / OrthodromicDistance.R);
        final double delta = angularThreshold / (double) granularity; // width and height of grid

        FlatMapFunction<Row, Row> transformInput = row -> {
            double[] p = parsePolygon(row.getString(1));
            double[] c = circleRepresentation(p);
            return getBlockIndices(p, delta).mapToObj(i -> RowFactory.create(i, p, row.get(0), c))
                    .iterator();
        };
        // transform inputs
        target = target.flatMap(transformInput, inputEncoder);
        source = source.flatMap(transformInput, inputEncoder);
        // broadcast target blocks
        Set<Long> ttb = new HashSet<>(
                target
                        .map((MapFunction<Row, Long>) r -> r.getLong(0), Encoders.LONG())
                        .distinct()
                        .collectAsList()
        );
        Broadcast<Set<Long>> tb = sc.broadcast(ttb);
        // create blockMap
        Dataset<Row> blockMap = source.map((MapFunction<Row, Long>) r -> r.getLong(0), Encoders.LONG())
                .distinct()
                .flatMap((FlatMapFunction<Long, Row>) sourceBlockId ->
                                getCorrespondingBlockIndices(sourceBlockId, granularity, delta, distanceThreshold)
                                        .filter(tb.getValue()::contains)
                                        .mapToObj(targetBlockId -> RowFactory.create(sourceBlockId, targetBlockId))
                                        .iterator()
                        , blockMapEncoder
                );

        // apply blockmap to append targetBlockId to rows
        source = source.join(blockMap, source.col("blockId").equalTo(blockMap.col("sourceBlockId")));

        source.repartition(source.col("targetBlockId"));
        target.repartition(target.col("blockId"));
        // compute similarities
        Dataset<Row> result = source
                .joinWith(target, source.col("targetBlockId").equalTo(target.col("blockId")))
                .map((MapFunction<Tuple2<Row, Row>, Row>) rows -> {
                    double[] p1 = rows._1.getList(1).stream().mapToDouble(a -> (Double)a).toArray();
                    double[] p2 = rows._2.getList(1).stream().mapToDouble(a -> (Double)a).toArray();
                    double[] c1 = rows._1.getList(3).stream().mapToDouble(a -> (Double)a).toArray();
                    double[] c2 = rows._2.getList(3).stream().mapToDouble(a -> (Double)a).toArray();
                    String sourceURL = rows._1().getString(2);
                    String targetURL = rows._2().getString(2);
                    double bound = lowerDistanceBound(c1, c2);
                    double distance = (bound > distanceThreshold) ? bound : polygonDistance(p1, p2, distanceThreshold);
                    return RowFactory.create(sourceURL, targetURL, distance);
                }, outputEncoder)
                .filter((FilterFunction<Row>) row -> row.getDouble(2) <= distanceThreshold)
                .map((MapFunction<Row, Row>) row -> RowFactory.create(
                        row.get(0), row.get(1), 1 / (1 + row.getDouble(2))
                ), outputEncoder)
                .withColumnRenamed("distance", "similarity");

        return result;
    }

    private static LongStream getBlockIndices(final double[] polygon, final double delta) {
        return IntStream.range(0, polygon.length/2)
                .mapToLong(i -> interleavedIndex(getIndex(polygon[2*i], delta), getIndex(polygon[2*i+1], delta)));
    }

    private static LongStream getCorrespondingBlockIndices(final long blockIndex, final int granularity,
                                                    final double delta, final double distanceThreshold) {
        List<Long> result = new ArrayList<>();
        final int latMin = getIndex(-90, delta);
        final int latMax = getIndex(90, delta) - 1;
        final int longMin = getIndex(-180, delta);
        final int longMax = getIndex(180, delta) - 1;
        final int[] componentIndices = decomposedIndex(blockIndex);
        final int latIndex = componentIndices[0];
        final int longIndex = componentIndices[1];
        for (int d = latIndex - granularity; d <= latIndex + granularity; d++) {
            // if crossing a pole, we need to update the index
            final int lat = d > latMax ? 2 * latMax - d : (d < latMin ? 2 * latMin - d : d);
            // if landing exactly on a tile around the pole, we must add all tiles around it
            if (lat == latMin || lat == latMax) {
                IntStream.range(longMin, longMax + 1)
                        .mapToLong(lon -> interleavedIndex(lat, lon))
                        .forEach(result::add);
            } else {
                int localGranularity = (int) Math.ceil(granularity /
                        Math.cos(delta * Math.toRadians(lat < 0 ? -lat : (lat + 1))));
                for (int e = -localGranularity; e <= localGranularity; e++) {
                    // adjust longitude for pole crossing (d != lat means pole was crossed)
                    int lon = (d != lat ? -(longIndex+1) : longIndex) + e;
                    // crossing 180 the 180° boundary means jumping to -180° and vice versa
                    lon += (lon > longMax ? -1 : (lon < longMin ? 1 : 0)) * 2 * (longMax + 1);
                    result.add(interleavedIndex(lat, lon));
                }
            }
        }
        // final filtering
        return result.stream().filter(index -> {
            int[] candidate = decomposedIndex(index);
            boolean earlyAccept = latIndex == candidate[0] && (
                    longIndex == candidate[1]
                            || latIndex == latMin
                            || latIndex == latMax
            );
            if (earlyAccept) return true;
            int lat1 = latIndex;
            int lat2 = candidate[0];
            switch (Integer.signum(lat2 - lat1)) {
                case 1: lat1 += 1; break;
                case -1: lat2 += 1; break;
            }
            int long1 = longIndex;
            int long2 = candidate[1];
            switch (Integer.signum(long2 - long1)) {
                case 1: long1 += 1; break;
                case -1: long2 += 1; break;
            }
            return OrthodromicDistance.getDistanceInDegrees(
                    lat1 * delta, long1 * delta, lat2 * delta, long2 * delta
            ) <= distanceThreshold;
        }).mapToLong(a -> a);
    }

    private static int getIndex(final double angle, final double delta) {
        return (int) Math.floor(angle / delta);
    }

    /**
     * Bijection from ℕ² -> ℕ
     * @param latIndex lat block index
     * @param longIndex long block index
     * @return composite single block index
     */
    private static long interleavedIndex (int latIndex, int longIndex) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result |= (latIndex & (1 << i)) << (i);
            result |= (longIndex & (1 << i)) << (i+1);
        }
        return result;
    }

    /**
     * Bijection from ℕ -> ℕ²
     * @param interleavedIndex composite block index
     * @return (lat, long) indices
     */
    private static int[] decomposedIndex (long interleavedIndex) {
        int[] result = {0,0};
        for (int i = 0; i < 32; i++) {
            result[0] |= (interleavedIndex >> i) & (1 << i);
            result[1] |= (interleavedIndex >> (1+i)) & (1 << i);
        }
        return result;
    }

    private static double[] parsePolygon(String data) {
        return Arrays.stream(data.split(",")).mapToDouble(Double::parseDouble).toArray();
    }

    private static double pointDistance(double lat1, double long1, double lat2, double long2) {
        return OrthodromicDistance.getDistanceInDegrees(lat1, long1, lat2, long2);
    }

    /**
     * bounding circle representation is {lat, long, radius}
     */
    private static double[] circleRepresentation(double[] polygon) {
        double radius = 0;
        double centerLat = polygon[0];
        double centerLong = polygon[1];
        for (int i = 0; i < polygon.length; i+=2) {
            final double lat1 = polygon[i];
            final double long1 = polygon[i+1];
            for (int j = i+2; j < polygon.length; j+=2) {
                final double lat2 = polygon[j];
                final double long2 = polygon[j+1];
                // radius of the bounding circle is the halfed max distance between two points
                final double d = pointDistance(lat1, long1, lat2, long2);
                final double r = d / 2;
                if (r > radius) {
                    radius = r;
                    // the center of the circle
                    centerLat = (lat1 + lat2) / 2;
                    centerLong = (long1 + long2) / 2;
                }
            }
        }
        return new double[]{centerLat, centerLong, radius};
    }

    private static double lowerDistanceBound(double[] circle1, double[] circle2) {
        return pointDistance(circle1[0], circle1[1], circle2[0], circle2[1]) - (circle1[2] + circle2[2]);
    }

    private static double polygonDistance(double[] p1, double[] p2, double distanceThreshold) {
        double max = 0f;
        for (int i = 0; i < p1.length; i+=2) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < p2.length; j+=2) {
                min = Math.min(min, pointDistance(p1[i],p1[i+1],p2[j],p2[j+1]));
            }
            if (min > distanceThreshold) return min;
            max = Math.max(max, min);
        }
        return max;
    }

}
