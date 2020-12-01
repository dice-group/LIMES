/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.pointsets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.CentroidIndexedHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.IndexedHausdorffMeasure;

/**
 * Still need to add tabu list. Basically checks whether two polygons have
 * already been compared and rejected Should also check whether the polygon
 * combination is already part of the result
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class GeoHR3 {
    // this angularThreshold in is degrees, thus need to convert km to degrees
    // when using
    // this index

    public static boolean threshold = false;
    public static double DEFAULT_THRESHOLD = 1f;
    public static int DEFAULT_GRANULARITY = 4;
    public static float delta;
    public boolean HR3;
    public IPointsetsMeasure setMeasure;
    public boolean verbose = false;
    public long indexingTime;
    protected int granularity;
    protected float angularThreshold;
    protected float distanceThreshold;
    int latMax, latMin, longMax, longMin;

    public GeoHR3(float distanceThreshold, int granularity, MeasureType hd) {
        this.distanceThreshold = distanceThreshold;
        this.angularThreshold = (float) ((distanceThreshold * 180) / (Math.PI * OrthodromicDistance.R));
        this.granularity = granularity;
        HR3 = true;
        if (distanceThreshold == 0) {
            angularThreshold = (float) ((0.001 * 180) / (Math.PI * OrthodromicDistance.R));
        }
        delta = angularThreshold / (float) granularity;
        latMax = (int) Math.floor(90f / delta) - 1; // we count 0 to the
        // positives
        latMin = (int) Math.floor(-90f / delta);
        longMax = (int) Math.floor(180f / delta) - 1;
        longMin = (int) Math.floor(-180f / delta);

        setMeasure = (IPointsetsMeasure) MeasureFactory.createMeasure(hd);

    }

    /**
     * Computes the geo squares for each polygon
     *
     * @param input
     *            Set of polygon to be indexed
     * @return Index for all polygons
     */
    public GeoIndex assignSquares(Set<Polygon> input) {
        GeoIndex index = new GeoIndex();
        for (Polygon p : input) {
            for (Point x : p.points) {
                int latIndex = (int) Math.floor(x.coordinates.get(0) / delta);
                int longIndex = (int) Math.floor(x.coordinates.get(1) / delta);
                if (verbose) {
                    System.out.println(p.uri + ": (" + latIndex + "," + longIndex + ")");
                }
                index.addPolygon(p, latIndex, longIndex);
            }
        }
        return index;
    }

    /**
     * Returns the squares to compare for a given index. This is the trickiest
     * part of the code 1 - The index runs from longMin to (-longMin-1) (e.g.,
     * from -6 to 5 for theta = 30°). Similar for the latitude 2 - The
     * granularity (i.e., the number of squares to add to the index) changes
     * with respect to the longitude at which one is. The new value is
     * granularity/cos(longitude). 3 - When picking the squares w.r.t. the
     * latitude, one sometimes crosses the pole. 4 - When picking the squares
     * w.r.t. the longitude, one sometimes crosses +180° or -180° 5 - When
     * selecting squares at the poles, one has to take all squares as the cos of
     * 90° is 0.
     *
     * @param latIndex
     *            Latitude index of square for which "neighbors" are required
     * @param longIndex
     *            Longitude index
     * @param index
     *            GeoIndex
     * @return List of "neighbors"
     */
    public Set<List<Integer>> getSquaresToCompare(int latIndex, int longIndex, GeoIndex index) {
        int lat, lon, realLat, realLong, localGranularity;
        Set<List<Integer>> toCompare = new HashSet<List<Integer>>();
        int polarCross; // stores whether a pole was crossed during the
        // computation of the index
        for (int deltaLat = (-1) * granularity; deltaLat <= granularity; deltaLat++) {
            // compute granularity for each latitude
            lat = latIndex + deltaLat;
            if (lat > latMax) {
                realLat = 2 * latMax - lat;
                polarCross = -1;
            } else if (lat < latMin) {
                realLat = 2 * latMin - lat;
                polarCross = -1;
            } else {
                realLat = lat;
                polarCross = 1;
            }

            // need to take care of special cases where the latitude is 180° or
            // -180°, i.e.,
            // we have reached the north or south pole
            if (realLat == latMax || realLat == latMin) {
                for (int deltaLong = longMin; deltaLong <= longMax; deltaLong++) {
                    if (index.getSquare(realLat, deltaLong).size() > 0) {
                        toCompare.add(Arrays.asList(new Integer[] { realLat, deltaLong }));
                    }
                }
            } // if latitude index is negative then take the circle above, i.e.,
              // else take the one below. Equivalent to taking the latitude
              // circle with the largest radius
            else {
                if (realLat < 0) {
                    localGranularity = (int) Math
                            .ceil(granularity / Math.cos(Math.abs(realLat) * delta * Math.PI / 180));
                } else {
                    localGranularity = (int) Math
                            .ceil(granularity / Math.cos((Math.abs(realLat) + 1) * delta * Math.PI / 180));
                }

                // if crossing occurred we need to alter the longIndex
                if (polarCross < 0) {
                    lon = -1 - longIndex;
                } else {
                    lon = longIndex;
                }

                for (int deltaLong = (-1) * localGranularity; deltaLong <= localGranularity; deltaLong++) {
                    realLong = deltaLong + lon;
                    // idea here is that index on positive side goes from 0 to
                    // longMax
                    // thus on negative side it goes from -1 to -(longMax +1)
                    // crossing the 180° boundary means jumping to -180° and
                    // vice versa
                    if (realLong > longMax) {
                        realLong = realLong - 2 * (longMax + 1);
                    } else if (realLong < (-1) * (longMax + 1)) {
                        realLong = 2 * (longMax + 1) + realLong;
                    }
                    toCompare.add(Arrays.asList(new Integer[] { realLat, realLong }));
                }
            }
        }

        if (HR3) {
            Set<List<Integer>> result = new HashSet<List<Integer>>();
            double lat1, lat2, long1, long2;
            for (List<Integer> candidate : toCompare) {
                // square is at the north-east of reference square then take
                // upper corner of reference and lower left corner of candidate
                if (latIndex == candidate.get(0) && longIndex == candidate.get(1)) {
                    result.add(candidate);
                } else if (latIndex == latMin && candidate.get(0) == latMin
                        || latIndex == latMax && candidate.get(0) == latMax) {
                    result.add(candidate);
                } else {
                    if (candidate.get(0) > latIndex) {
                        lat1 = latIndex + 1;
                        lat2 = candidate.get(0);
                    } else if (candidate.get(0) < latIndex) {
                        lat1 = latIndex;
                        lat2 = candidate.get(0) + 1;
                    } // else they are the same value. No need to alter that
                    else {
                        lat1 = latIndex;
                        lat2 = candidate.get(0);
                    }
                    if (candidate.get(1) > longIndex) {
                        long1 = longIndex + 1;
                        long2 = candidate.get(1);
                    } else if (candidate.get(1) < longIndex) {
                        long1 = longIndex;
                        long2 = candidate.get(1) + 1;
                    } else {
                        long1 = longIndex;
                        long2 = candidate.get(1);
                    }

                    lat1 = lat1 * delta;
                    lat2 = lat2 * delta;
                    long1 = long1 * delta;
                    long2 = long2 * delta;

                    double d = OrthodromicDistance.getDistanceInDegrees(lat1, long1, lat2, long2);
                    if (d <= distanceThreshold) {
                        result.add(candidate);
                    }
                }
            }
            if (verbose) {
                System.out.println("HR3: (" + latIndex + "," + longIndex + ") => " + result);
            }
            return result;
        }
        if (verbose) {
            System.out.println("NoHR3: (" + latIndex + "," + longIndex + ") => " + toCompare);
        }
        return toCompare;
    }

    /**
     * Runs GeoHR3 for source and target dataset. Uses the set SetMeasure
     * implementation. FastHausdorff is used as default
     *
     * @param sourceData,
     *            Source polygons
     * @param targetData,
     *            Target polygons
     * @return Mapping of polygons
     */
    public AMapping run(Set<Polygon> sourceData, Set<Polygon> targetData) {
        long begin = System.currentTimeMillis();
        GeoIndex source = assignSquares(sourceData);
        GeoIndex target = assignSquares(targetData);
        long end = System.currentTimeMillis();
        Map<String, Set<String>> computed = new HashMap<String, Set<String>>();
        indexingTime = end - begin;
        if (verbose) {
            System.out.println("Geo-Indexing took: " + indexingTime + " ms");
            System.out.println("|Source squares|= " + source.squares.keySet().size());
            System.out.println("|Target squares|= " + target.squares.keySet().size());
            System.out.println("Distance Threshold = " + distanceThreshold);
            System.out.println("Angular Threshold = " + angularThreshold);
            System.out.println("Index = " + source);
        }
        AMapping m = MappingFactory.createDefaultMapping();

        double d;
        if (setMeasure instanceof CentroidIndexedHausdorffMeasure) {
            ((CentroidIndexedHausdorffMeasure) setMeasure).computeIndexes(sourceData, targetData);
        } else if (setMeasure instanceof IndexedHausdorffMeasure) {
            PolygonIndex targetIndex = new PolygonIndex();
            targetIndex.index(targetData);
            ((IndexedHausdorffMeasure) setMeasure).targetIndex = targetIndex;
        }
        for (Integer latIndex : source.squares.keySet()) {
            for (Integer longIndex : source.squares.get(latIndex).keySet()) {
                GeoSquare g1 = source.getSquare(latIndex, longIndex);
                Set<List<Integer>> squares = getSquaresToCompare(latIndex, longIndex, target);
                for (List<Integer> squareIndex : squares) {
                    GeoSquare g2 = target.getSquare(squareIndex.get(0), squareIndex.get(1));
                    // only run if the hypercube actually exists
                    for (Polygon a : g1.elements) {
                        for (Polygon b : g2.elements) {
                            if (!computed.containsKey(a.uri)) {
                                computed.put(a.uri, new HashSet<String>());
                            }
                            if (!computed.get(a.uri).contains(b.uri)) {
                                // add subset condition
                                d = setMeasure.computeDistance(a, b, distanceThreshold);
                                if (d <= distanceThreshold) {
                                    m.add(a.uri, b.uri, 1 / (1 + d));
                                }
                            }
                            computed.get(a.uri).add(b.uri);
                        }
                    }
                }
            }
        }
        return m;
    }
}
