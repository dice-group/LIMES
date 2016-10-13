/**
 *
 */
package org.aksw.limes.core.measures.measure.pointsets.surjection;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.APointsetsMeasure;

/**
 *         <p>
 *         enhanced variant of the surjection, in which the surjection
 *         must be fair. The surjection between sets X and Y is fair if η' maps
 *         lements of X as evenly as possible to Y.
 */
/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public class FairSurjectionFinder extends SurjectionFinder {

    /**
     * @param X
     *            source polygon
     * @param Y
     *            target polygon
     */
    FairSurjectionFinder(Polygon X, Polygon Y) {
        super(X, Y);
    }

    /**
     * @return List of fair surjection pairs
     */
    public List<PairSimilar<Point>> getFairSurjectionPairsList() {
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
                    surjectionPairsList.add(new PairSimilar<Point>(l, s));
                    fairCount++;
                    // if the fair capacity reached the go to the next point
                    if (fairCount == fairCapacity)
                        break;
                }
            }
        }
        return surjectionPairsList;
    }

    /**
     * @param x
     *            Point
     * @param Y
     *            Point
     * @return SortedNearestPoints
     */
    TreeMap<Double, Point> getSortedNearestPoints(Point x, Polygon Y) {
        TreeMap<Double, Point> result = new TreeMap<Double, Point>();
        for (Point y : Y.points) {
            result.put(APointsetsMeasure.pointToPointDistance(x, y), y);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aksw.limes.core.measures.measure.pointsets.surjection.
     * SurjectionFinder#getRuntimeApproximation(double)
     */
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}