package org.aksw.limes.core.measures.measure.pointsets;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.Measure;

public abstract class PointsetsMeasure extends Measure implements IPointsetsMeasure{
	
	protected static int computations;
	public static boolean USE_GREAT_ELLIPTIC_DISTANCE = false;
	
	/* (non-Javadoc)
	 * @see org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(java.lang.Object, java.lang.Object)
	 */
	public double getSimilarity(Object a, Object b) {
		Polygon p1 = OrchidMapper.getPolygon((String) a);
		Polygon p2 = OrchidMapper.getPolygon((String) b);
		double d = computeDistance(p1, p2, 0f);
		return 1d / (1d + (double) d);
	}


	/**
	 * @param x Point x
	 * @param y Point y
	 * @return Point-to-point distance between x and y
	 */
	public static double pointToPointDistance(Point x, Point y) {
		computations++;
		if (USE_GREAT_ELLIPTIC_DISTANCE) {
			return GreatEllipticDistance.getDistanceInDegrees(x, y);
		}
		return OrthodromicDistance.getDistanceInDegrees(x, y);
	}
	
}
