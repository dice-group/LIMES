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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.distance.Frechet;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public class GeOxygeneFrechetMeasure extends APointsetsMeasure {

    public GeOxygeneFrechetMeasure() {
        computations = 0;
    }

    /**
     * Convert polygon to ILineSegment
     *
     * @param poly Polygon
     * @return ILineSegment out of the input poly
     */
    public static ILineSegment toLineSegment(Polygon poly) {
        // assume a polygon as a curve starting at point p and end at point q
        double pLat = poly.points.get(0).coordinates.get(0);
        double pLong = poly.points.get(0).coordinates.get(1);
        double qLat = poly.points.get(poly.points.size() - 1).coordinates.get(0);
        double qLong = poly.points.get(poly.points.size() - 1).coordinates.get(1);
        GM_LineSegment lineSegment = new GM_LineSegment(new DirectPosition(pLat, pLong),
                new DirectPosition(qLat, qLong));

        for (int i = 1; i < (poly.points.size() - 1); i++) {
            double lat = poly.points.get(i).coordinates.get(0);
            double lon = poly.points.get(i).coordinates.get(1);
            lineSegment.addControlPoint(new DirectPosition(lat, lon));
        }

        return lineSegment;
    }

    /**
     * @param X Polygon
     * @param Y Polygon
     * @param threshold  distance threshold
     * @return the GeOxygeneFrechet distance between X and Y
     */
    public static double distance(Polygon X, Polygon Y, double threshold) {
        return new GeOxygeneFrechetMeasure().computeDistance(X, Y, threshold);
    }

    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        // PrintStream originalStream = System.out;
//        System.setOut(null);
        double f = OrthodromicFrechetDistance.discreteFrechet(toLineSegment(X), toLineSegment(Y));
        
        // System.setOut(originalStream);
        return f;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#
     * getComputations()
     */
    public int getComputations() {
        return computations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.IMeasure#getName()
     */
    public String getName() {
        return "GeOxygeneFrechet";
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
     * @return Mapping from source to target resources
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
