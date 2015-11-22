/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.surjection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.limes.core.data.Instance;
import org.aksw.limes.core.data.Point;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.PolygonIndex;
import org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure;
import org.aksw.limes.core.util.Pair;

/**
 * @author sherif
 * class to find the surjection of the larger polygon to the smaller one.
 */
public class SurjectionFinder {
	public static boolean USE_GREAT_ELLIPTIC_DISTANCE = true;
	protected List<Pair<Point>> surjectionPairsList;
	protected Polygon small, large;


	/**
	 * @param X
	 * @param Y
	 *@author sherif
	 */
	SurjectionFinder(Polygon X, Polygon Y){
		surjectionPairsList = new ArrayList<Pair<Point>>();
		if(X.points.size() < Y.points.size()){ 
			small = X;
			large = Y;
		}else{
			small = Y;
			large = X;
		}
	}

	public List<Pair<Point>> getSurjectionPairsList() {
		if(surjectionPairsList.isEmpty()){
			Polygon largeCopy = new Polygon(large);
			
			// find nearest points (l) to each point of the small polygon (s)
			// and add the pairs (l,s) to the surjectionPairsList
			for (Point s: small.points) {
				Point l = getNearestPoint(s, largeCopy);
				surjectionPairsList.add(new Pair<Point>(l,s));
				largeCopy.remove(l);
			}

			// for each of the rest points of the large polygon (l)
			// find nearest point (s) from the small polygon 
			// and add the pairs (l,s) to the surjectionPairsList
			for (Point l: largeCopy.points) {
				Point s = getNearestPoint(l, small);
				surjectionPairsList.add(new Pair<Point>(l,s));
			}
		}
		return surjectionPairsList;
	}

	
    /**
     * @param x Point x
     * @param y Point y
     * @return Distance between x and y
     */
    public double distance(Point x, Point y) {
        if(USE_GREAT_ELLIPTIC_DISTANCE){
        	return GreatEllipticDistance.getDistanceInDegrees(x,y);
        }
        return OrthodromicDistance.getDistanceInDegrees(x, y);
    }

	protected Point getNearestPoint(Point x, Polygon Y){
		double d, min = Double.MAX_VALUE;
		Point result = null;
		for (Point y : Y.points) {
			d = distance(x, y); 
			if( d < min){
				min = d;
				result = y;
			}
		}
		return result;
	}
	
	


	public double getRuntimeApproximation(double mappingSize) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static void test() {
		Point a1 = new Point("a1", Arrays.asList(new Double[]{1.0, 1.0}));
		Point b1 = new Point("b1", Arrays.asList(new Double[]{1.0, 2.0}));
		Point c1 = new Point("c1", Arrays.asList(new Double[]{2.0, 1.0}));
		Polygon A = new Polygon("A", Arrays.asList(new Point[]{a1, b1, c1}));

		Point a2 = new Point("a2", Arrays.asList(new Double[]{3.0, 1.0}));
		Point b2 = new Point("b2", Arrays.asList(new Double[]{3.0, 2.0}));
		Point c2 = new Point("c2", Arrays.asList(new Double[]{2.0, 2.0}));
		Point d2 = new Point("d2", Arrays.asList(new Double[]{2.0, 7.0}));
		Point e2 = new Point("e2", Arrays.asList(new Double[]{5.0, 2.0}));
		Point f2 = new Point("f2", Arrays.asList(new Double[]{2.0, 5.0}));
		Point g2 = new Point("g2", Arrays.asList(new Double[]{6.0, 7.0}));
		Point h2 = new Point("h2", Arrays.asList(new Double[]{5.0, 7.0}));
		Point i2 = new Point("i2", Arrays.asList(new Double[]{5.0, 6.0}));
		Polygon B = new Polygon("B", Arrays.asList(new Point[]{a2, b2, c2, d2, e2, f2, g2, h2, i2}));

		SurjectionFinder sf = new SurjectionFinder(A, B);
		for(Pair<Point> p : sf.getSurjectionPairsList()){
			System.out.println(p.a.label + "<-->" + p.b.label);
		}
	}

	public static void main(String args[]) {
		test();
	}
}
