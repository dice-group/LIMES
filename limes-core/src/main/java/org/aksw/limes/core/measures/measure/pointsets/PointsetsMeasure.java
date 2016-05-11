package org.aksw.limes.core.measures.measure.pointsets;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.Measure;

public abstract class PointsetsMeasure extends Measure implements IPointsetsMeasure{
	
	protected static int computations;
	public static boolean USE_GREAT_ELLIPTIC_DISTANCE = false;

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
