package org.aksw.limes.core.measures.measure.pointsets;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.Measure;

public abstract class PointsetsMeasure extends Measure implements IPointsetsMeasure {

    public static boolean USE_GREAT_ELLIPTIC_DISTANCE = false;
    protected static int computations;

    /**
     * @param x
     *         Point x
     * @param y
     *         Point y
     * @return Point-to-point distance between x and y
     */
    public static double pointToPointDistance(Point x, Point y) {
        computations++;
        if (USE_GREAT_ELLIPTIC_DISTANCE) {
            return GreatEllipticDistance.getDistanceInDegrees(x, y);
        }
        return OrthodromicDistance.getDistanceInDegrees(x, y);
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(java.lang.Object, java.lang.Object)
     */
    public double getSimilarity(Object a, Object b) {
        Polygon p1 = OrchidMapper.getPolygon((String) a);
        Polygon p2 = OrchidMapper.getPolygon((String) b);
        double d = computeDistance(p1, p2, 0f);
        return 1d / (1d + (double) d);
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#getComputations()
     */
    public int getComputations() {
        return computations;
    }

}
