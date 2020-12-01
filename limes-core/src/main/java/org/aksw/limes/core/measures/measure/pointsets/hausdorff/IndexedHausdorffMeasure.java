/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.mapper.pointsets.PolygonIndex;
import org.aksw.limes.core.measures.measure.pointsets.APointsetsMeasure;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class IndexedHausdorffMeasure extends APointsetsMeasure {

    public PolygonIndex targetIndex;
    public NaiveHausdorffMeasure nh;

    /**
     * Initialization ensures that application fails if points were not indexed
     * before distances are computed
     */
    public IndexedHausdorffMeasure() {
        targetIndex = null;
        computations = 0;
        nh = new NaiveHausdorffMeasure();
    }

    public int getComputations() {
        return computations + targetIndex.computations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#run(java
     * .util.Set, java.util.Set, double)
     */
    public AMapping run(Set<Polygon> source, Set<Polygon> target, double threshold) {
        // first run indexing
        AMapping m = MappingFactory.createDefaultMapping();
        targetIndex = new PolygonIndex();
        targetIndex.index(target);
        double d;
        for (Polygon s : source) {
            for (Polygon t : target) {
                d = computeDistance(s, t, threshold);
                if (d <= threshold) {
                    m.add(s.uri, t.uri, d);
                }
            }
        }
        return m;
    }

    /**
     * @param s Polygon
     * @return Inner distances of s
     */
    public Map<Point, Map<Point, Double>> getInnerDistances(Polygon s) {
        Map<Point, Map<Point, Double>> distances = new HashMap<Point, Map<Point, Double>>();
        for (int i = 0; i < s.points.size(); i++) {
            Map<Point, Double> buffer = new HashMap<Point, Double>();
            for (int j = i + 1; j < s.points.size(); j++) {
                buffer.put(s.points.get(j), pointToPointDistance(s.points.get(i), s.points.get(j)));
            }
            distances.put(s.points.get(i), buffer);
        }
        return distances;
    }

    public Map<String, Map<String, Double>> runWithoutIndex(Set<Polygon> source, Set<Polygon> target,
            double threshold) {

        Map<String, Map<String, Double>> map = new HashMap<String, Map<String, Double>>();
        Map<String, Double> mapping;
        Map<Point, Map<Point, Double>> distances;
        Map<Point, Map<Point, Double>> exemplars;
        double min, max, d;
        boolean approximationWorked;
        for (Polygon s : source) {
            distances = getInnerDistances(s);
            // now run approximation
            mapping = new HashMap<String, Double>();
            for (Polygon t : target) {
                max = 0f;
                exemplars = new HashMap<Point, Map<Point, Double>>();
                for (Point x : s.points) {
                    // no exemplars yet, then simply compute distance to all
                    // points y
                    if (exemplars.isEmpty()) {
                        min = Double.POSITIVE_INFINITY;
                        for (Point y : t.points) {
                            d = pointToPointDistance(x, y);
                            if (!exemplars.containsKey(x)) {
                                exemplars.put(x, new HashMap<Point, Double>());
                            }
                            exemplars.get(x).put(y, d);
                            if (d < min) {
                                min = d;
                            }
                        }

                    } // else first try approximations
                    else {
                        // try each exemplar to point combination
                        min = Double.POSITIVE_INFINITY;
                        for (Point y : t.points) {
                            approximationWorked = false;
                            for (Point e : exemplars.keySet()) {
                                double approximation = 0;
                                // check whether distance from y to examplar was
                                // actually computed
                                if (exemplars.get(e).containsKey(y)) {
                                    if (s.points.indexOf(x) < s.points.indexOf(e)) {
                                        approximation = Math.abs(distances.get(x).get(e) - exemplars.get(e).get(y));
                                    } else {
                                        approximation = Math.abs(distances.get(e).get(x) - exemplars.get(e).get(y));
                                    }
                                }
                                if (approximation > threshold) {
                                    approximationWorked = true;
                                    break;
                                }
                            }
                            if (!approximationWorked) {
                                d = pointToPointDistance(x, y);
                                // update exemplars
                                if (!exemplars.containsKey(x)) {
                                    exemplars.put(x, new HashMap<Point, Double>());
                                }
                                exemplars.get(x).put(y, d);
                                if (min > d) {
                                    min = d;
                                }
                            }
                        }
                    }
                    // update maximal distances
                    // note that in case an approximation
                    if (max < min) {
                        max = min;
                    }
                    if (max > threshold) {
                        break;
                    }
                }

                if (max <= threshold) {
                    mapping.put(t.uri, max);
                }
            }
            if (!mapping.isEmpty()) {
                map.put(s.uri, mapping);
            }
        }
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#
     * computeDistance(org.aksw.limes.core.measures.mapper.atomic.hausdorff.
     * Polygon, org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon,
     * double)
     */
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        if (X.uri.equals(Y.uri)) {
            return 0f;
        }
        double max = 0f;
        double d;
        Map<Point, Double> distances;
        double min = 0, approx;
        for (Point x : X.points) {
            distances = new HashMap<Point, Double>();
            for (Point y : Y.points) {
                if (distances.isEmpty()) {
                    min = pointToPointDistance(x, y);
                    distances.put(y, min);
                } else {
                    // first try examplars
                    double dist, minDist = Double.POSITIVE_INFINITY;
                    Point exemplar = null;
                    for (Point e : distances.keySet()) {
                        dist = targetIndex.getDistance(Y.uri, e, y);
                        if (dist < minDist) {
                            minDist = dist;
                            exemplar = e;
                        }
                    }
                    approx = Math.abs(distances.get(exemplar) - minDist);
                    if (approx > threshold) {
                        // no need to compute d as it is larger than the
                        // threshold anyway
                        // also no need to update min as the value would lead to
                        // the point
                        // being discarded anyway
                        d = threshold + 1;
                        // distances.put(y, d);
                        if (min > d) {
                            min = d;
                        }
                    } else if (approx < min) {
                        // approximation does not give us any information
                        d = pointToPointDistance(x, y);
                        distances.put(y, d);
                        if (min > d) {
                            min = d;
                        }
                    }
                }
            }
            if (max < min) {
                max = min;
            }
            if (max > threshold) {
                return max;
            }
        }

        return max;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.IMeasure#getName()
     */
    public String getName() {
        return "indexedHausdorff";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.pointsets.PointsetsMeasure#
     * getSimilarity(java.lang.Object, java.lang.Object)
     */
    public double getSimilarity(Object object1, Object object2) {
        return nh.getSimilarity(object1, object2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.IMeasure#getType()
     */
    public String getType() {
        return nh.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(org.aksw.
     * limes.core.io.cache.Instance, org.aksw.limes.core.io.cache.Instance,
     * java.lang.String, java.lang.String)
     */
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        return nh.getSimilarity(instance1, instance2, property1, property2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.aksw.limes.core.measures.measure.IMeasure#getRuntimeApproximation(
     * double)
     */
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
