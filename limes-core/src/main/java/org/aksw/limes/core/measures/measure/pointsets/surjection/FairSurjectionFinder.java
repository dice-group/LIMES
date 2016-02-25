/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.surjection;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.util.Pair;

/**
 * @author sherif enhanced variant of the surjection, in which the surjection
 *         must be fair. The surjection between sets X and Y is fair if η' maps
 *         elements of X as evenly as possible to Y.
 */
public class FairSurjectionFinder extends SurjectionFinder {
    public static boolean USE_GREAT_ELLIPTIC_DISTANCE = true;

    FairSurjectionFinder(Polygon X, Polygon Y) {
	super(X, Y);
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

    public List<Pair<Point>> getFairSurjectionPairsList() {
	if (surjectionPairsList.isEmpty()) {
	    // compute the fair capacity for each of the small polygon points
	    int fairCapacity = (int) Math.ceil((double) large.points.size() / (double) small.points.size());
	    for (Point s : small.points) {
		int fairCount = 0;
		// get sorted set of all near by points
		TreeMap<Double, Point> nearestPoints = getSortedNearestPoints(s, large);
		// add fairCapacity times of the nearby point to the
		// surjectionPairsList
		for (Entry<Double, Point> e : nearestPoints.entrySet()) {
		    Point l = e.getValue();
		    surjectionPairsList.add(new Pair<Point>(l, s));
		    fairCount++;
		    // if the fair capacity reached the go to the next point
		    if (fairCount == fairCapacity)
			break;
		}
	    }
	}
	return surjectionPairsList;
    }

    TreeMap<Double, Point> getSortedNearestPoints(Point x, Polygon Y) {
	TreeMap<Double, Point> result = new TreeMap<Double, Point>();
	for (Point y : Y.points) {
	    result.put(distance(x, y), y);
	}
	return result;
    }

    public double getRuntimeApproximation(double mappingSize) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

}
