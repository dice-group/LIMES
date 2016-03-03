package org.aksw.limes.core.measures.measure.pointsets.frechet;


import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.PointsetsMeasure;

import fr.ign.cogit.geoxygene.distance.Frechet;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;

/**
 * @author sherif
 *
 */
public class GeOxygeneFrechet extends PointsetsMeasure {

    public int computations;

    public double computeDistance(Polygon X, Polygon Y, double threshold) {
	// PrintStream originalStream = System.out;
	System.setOut(null);
	double f = Frechet.discreteFrechet(toLineSegment(X), toLineSegment(Y));
	// System.setOut(originalStream);
	return f;
    }

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
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @return Distance between the two polygons
     */
    public GeOxygeneFrechet() {
	computations = 0;
    }

    public int getComputations() {
	return computations;
    }

    public static double distance(Polygon X, Polygon Y, double threshold) {
	return new GeOxygeneFrechet().computeDistance(X, Y, threshold);
    }

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
     * @return Mapping of uris
     */
    public Mapping run(Set<Polygon> source, Set<Polygon> target, double threshold) {
	Mapping m = new MemoryMapping();
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

    /**
     * @param x
     *            Point x
     * @param y
     *            Point y
     * @return Distance between x and y
     */
    public double distance(Point x, Point y) {
	computations++;
	if (USE_GREAT_ELLIPTIC_DISTANCE) {
	    return GreatEllipticDistance.getDistanceInDegrees(x, y);
	}
	return OrthodromicDistance.getDistanceInDegrees(x, y);
    }

    public double getSimilarity(Object a, Object b) {
	Polygon p1 = OrchidMapper.getPolygon((String) a);
	Polygon p2 = OrchidMapper.getPolygon((String) b);
	double d = computeDistance(p1, p2, 0);
	return 1d / (1d + (double) d);
    }

    public String getType() {
	return "geodistance";
    }

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

    public double getRuntimeApproximation(double mappingSize) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

}
