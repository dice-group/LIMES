/**
 *
 */
package org.aksw.limes.core.measures.measure.pointsets.frechet;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.APointsetsMeasure;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.util.GeometricShapeFactory;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public class NaiveFrechetMeasure extends APointsetsMeasure {
    static GeometricShapeFactory gsf = new GeometricShapeFactory();
    static GeometryFactory gf = new GeometryFactory();
    static double delta = 0.01;
    public double[][] a, b, c, d;
    public int computations;
    Polygon poly1, poly2;

    public NaiveFrechetMeasure() {
        computations = 0;
    }

    /**
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @param threshold,
     *            the distance threshold
     * @return Distance between X and Y
     */
    public static double distance(Polygon X, Polygon Y, double threshold) {
        return new NaiveFrechetMeasure().computeDistance(X, Y, threshold);
    }

    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        PolygonFrechetDistance frechetDistance = new PolygonFrechetDistance(X, Y);
        return frechetDistance.computeFrechetDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.IMeasure#getName()
     */
    public String getName() {
        return "naiveFrechet";
    }

    /**
     * Computes the SetMeasure distance for a source and target set
     *
     * @param source
     *            Source polygons
     * @param target
     *            Target polygons
     * @param threshold
     *            Distance threshold
     * @return Mapping from source to rtarget resources
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

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.IMeasure#getType()
     */
    public String getType() {
        return "geodistance";
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
        TreeSet<String> source = instance1.getProperty(property1);
        TreeSet<String> target = instance2.getProperty(property2);
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
