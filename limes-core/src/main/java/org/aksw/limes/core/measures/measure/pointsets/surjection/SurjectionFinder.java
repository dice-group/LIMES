/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.surjection;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.Point;

import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.util.Pair;

/**
 * @author sherif class to find the surjection of the larger polygon to the
 *         smaller one.
 */
public class SurjectionFinder {
    public static boolean USE_GREAT_ELLIPTIC_DISTANCE = true;
    protected List<Pair<Point>> surjectionPairsList;
    protected Polygon small, large;

    /**
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @author sherif
     */
    SurjectionFinder(Polygon X, Polygon Y) {
	surjectionPairsList = new ArrayList<Pair<Point>>();
	if (X.points.size() < Y.points.size()) {
	    small = X;
	    large = Y;
	} else {
	    small = Y;
	    large = X;
	}
    }

    public List<Pair<Point>> getSurjectionPairsList() {
	if (surjectionPairsList.isEmpty()) {
	    Polygon largeCopy = new Polygon(large);

	    // find nearest points (l) to each point of the small polygon (s)
	    // and add the pairs (l,s) to the surjectionPairsList
	    for (Point s : small.points) {
		Point l = getNearestPoint(s, largeCopy);
		surjectionPairsList.add(new Pair<Point>(l, s));
		largeCopy.remove(l);
	    }

	    // for each of the rest points of the large polygon (l)
	    // find nearest point (s) from the small polygon
	    // and add the pairs (l,s) to the surjectionPairsList
	    for (Point l : largeCopy.points) {
		Point s = getNearestPoint(l, small);
		surjectionPairsList.add(new Pair<Point>(l, s));
	    }
	}
	return surjectionPairsList;
    }

    /**
     * @param x
     *            Point x
     * @param y
     *            Point y
     * @return Distance between x and y
     */
    public double distance(Point x, Point y) {
	if (USE_GREAT_ELLIPTIC_DISTANCE) {
	    return GreatEllipticDistance.getDistanceInDegrees(x, y);
	}
	return OrthodromicDistance.getDistanceInDegrees(x, y);
    }

    protected Point getNearestPoint(Point x, Polygon Y) {
	double d, min = Double.MAX_VALUE;
	Point result = null;
	for (Point y : Y.points) {
	    d = distance(x, y);
	    if (d < min) {
		min = d;
		result = y;
	    }
	}
	return result;
    }

    public double getRuntimeApproximation(double mappingSize) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

}
