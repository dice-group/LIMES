/**
 *
 */
package org.aksw.limes.core.measures.measure.pointsets.min;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.PointsetsMeasure;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author sherif
 */
public class NaiveMin extends PointsetsMeasure {

    /**
     * Brute force approach to computing the MAX distance between two polygons
     *
     * @param X
     *         First polygon
     * @param Y
     *         Second polygon
     * @return Distance between the two polygons
     */
    public NaiveMin() {
        computations = 0;
    }

    /**
     * @param X
     *         First polygon
     * @param Y
     *         Second polygon
     * @param threshold
     * @return
     */
    public static double distance(Polygon X, Polygon Y, double threshold) {
        double min = Double.MAX_VALUE;
        double d;
        for (Point x : X.points) {
            for (Point y : Y.points) {
                d = pointToPointDistance(x, y);
                if (min < d) {
                    min = d;
                }
            }

        }
        return min;
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#computeDistance(org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon, org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon, double)
     */
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        double min = Double.MAX_VALUE;
        double d;
        for (Point x : X.points) {
            for (Point y : Y.points) {
                d = pointToPointDistance(x, y);
                if (min > d) {
                    min = d;
                }
            }
        }
        return min;
    }

    public String getName() {
        return "naiveMin";
    }

    /**
     * Computes the SetMeasure distance for a source and target set
     *
     * @param source
     *         Source polygons
     * @param target
     *         Target polygons
     * @param threshold
     *         Distance threshold
     * @return Mapping of resources from Source to Target
     */
    public AMapping run(Set<Polygon> source, Set<Polygon> target, double threshold) {
        AMapping m = MappingFactory.createDefaultMapping();
        for (Polygon s : source) {
            for (Polygon t : target) {
                double d = computeDistance(s, t, threshold);
                if (d <= threshold) {
                    m.add(s.uri, t.uri, d);
                }
            }
        }
        return m;
    }


    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.IMeasure#getType()
     */
    public String getType() {
        return "geodistance";
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(org.aksw.limes.core.io.cache.Instance, org.aksw.limes.core.io.cache.Instance, java.lang.String, java.lang.String)
     */
    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
        TreeSet<String> source = a.getProperty(property1);
        TreeSet<String> target = b.getProperty(property2);
        Set<Polygon> sourcePolygons = new HashSet<Polygon>();
        Set<Polygon> targetPolygons = new HashSet<Polygon>();
        for (String s : source) {
            sourcePolygons.add(OrchidMapper.getPolygon(s));
        }
        for (String t : target) {
            targetPolygons.add(OrchidMapper.getPolygon(t));
        }
        double min = Double.MAX_VALUE;
        double d = 0;
        for (Polygon p1 : sourcePolygons) {
            for (Polygon p2 : targetPolygons) {
                d = computeDistance(p1, p2, 0);
                if (d < min) {
                    min = d;
                }
            }
        }
        return 1d / (1d + (double) d);
    }


    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.IMeasure#getRuntimeApproximation(double)
     */
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}
