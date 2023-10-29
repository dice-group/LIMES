package org.aksw.limes.spark;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import static org.aksw.limes.spark.OrchidHelpers.*;

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
    private static final ExpressionEncoder<Row> blockMapEncoder = RowEncoder.apply(
            new StructType()
                    .add("sourceBlockId", DataTypes.LongType, false)
                    .add("targetBlockId", DataTypes.LongType, false)
    );
    public static final ExpressionEncoder<Row> outputEncoder = RowEncoder.apply(
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

        // repartition
        source = source.repartition(Main.partitions, source.col("blockId"));
        target = target.repartition(Main.partitions, target.col("blockId"));

        // create blockMap
        Dataset<Row> blockMap = source.map((MapFunction<Row, Long>) r -> r.getLong(0), Encoders.LONG())
                .distinct()
                .flatMap((FlatMapFunction<Long, Row>) sourceBlockId ->
                                getCorrespondingBlockIndices(sourceBlockId, granularity, delta, distanceThreshold)
                                        .mapToObj(targetBlockId -> RowFactory.create(sourceBlockId, targetBlockId))
                                        .iterator()
                        , blockMapEncoder
                );

        // apply blockmap to append targetBlockId to rows
        source = source.join(blockMap, source.col("blockId").equalTo(blockMap.col("sourceBlockId")));

        source = source.repartition(Main.partitions, source.col("targetBlockId"));
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


}
