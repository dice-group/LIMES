package org.aksw.limes.spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 *
 */
public class OrchidHelpers {

    public static LongStream getBlockIndices(final double[] polygon, final double delta) {
        return IntStream.range(0, polygon.length/2)
                .mapToLong(i -> interleavedIndex(getIndex(polygon[2*i], delta), getIndex(polygon[2*i+1], delta)));
    }

    public static LongStream getCorrespondingBlockIndices(final long blockIndex, final int granularity,
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

    public static int getIndex(final double angle, final double delta) {
        return (int) Math.floor(angle / delta);
    }

    /**
     * Bijection from ℕ² -> ℕ
     * @param latIndex lat block index
     * @param longIndex long block index
     * @return composite single block index
     */
    public static long interleavedIndex (int latIndex, int longIndex) {
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
    public static int[] decomposedIndex (long interleavedIndex) {
        int[] result = {0,0};
        for (int i = 0; i < 32; i++) {
            result[0] |= (interleavedIndex >> i) & (1 << i);
            result[1] |= (interleavedIndex >> (1+i)) & (1 << i);
        }
        return result;
    }

    public static double[] parsePolygon(String data) {
        return Arrays.stream(data.split(",")).mapToDouble(Double::parseDouble).toArray();
    }

    public static double pointDistance(double lat1, double long1, double lat2, double long2) {
        return OrthodromicDistance.getDistanceInDegrees(lat1, long1, lat2, long2);
    }

    /**
     * bounding circle representation is {lat, long, radius}
     */
    public static double[] circleRepresentation(double[] polygon) {
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

    public static double lowerDistanceBound(double[] circle1, double[] circle2) {
        return pointDistance(circle1[0], circle1[1], circle2[0], circle2[1]) - (circle1[2] + circle2[2]);
    }

    public static double polygonDistance(double[] p1, double[] p2, double distanceThreshold) {
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
