/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.sumofmin;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMin;
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.PointsetsMeasure;

/**
 * @author sherif
 *
 */
public class NaiveSumOfMin extends PointsetsMeasure {
    public int computations;

    /**
     * Brute force approach to computing the MAX distance between two polygons
     *
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @return Distance between the two polygons
     */
    public NaiveSumOfMin() {
	computations = 0;
    }

    public int getComputations() {
	return computations;
    }

    public double computeDistance(Polygon X, Polygon Y, double threshold) {
	return (SumOfMins(X, Y) + SumOfMins(Y, X)) / 2;
    }

    private double SumOfMins(Polygon X, Polygon Y) {
	double sum = 0;
	for (Point x : X.points) {
	    sum += computeMinDistance(x, Y);
	}
	return sum;
    }

    private double computeMinDistance(Point x, Polygon Y) {
	double d, min = Double.MAX_VALUE;
	for (Point y : Y.points) {
	    d = distance(x, y);
	    if (d < min) {
		min = d;
	    }
	}
	return min;
    }

    public static double distance(Polygon X, Polygon Y, double threshold) {
	NaiveMin m = new NaiveMin();
	return (m.computeDistance(X, Y, threshold) + m.computeDistance(Y, X, threshold)) / 2;
    }

    public String getName() {
	return "naiveSumOfMin";
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
	return mappingSize / 1000d;
    }

}
