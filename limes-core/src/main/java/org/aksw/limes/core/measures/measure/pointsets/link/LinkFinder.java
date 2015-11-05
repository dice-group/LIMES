/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.link;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aksw.limes.core.data.Instance;
import org.aksw.limes.core.data.Point;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.PolygonIndex;
import org.aksw.limes.core.measures.measure.pointsets.SetMeasure;

/**
 * @author sherif
 * class to generate the link pairs between 2 polygons 
 */
public class LinkFinder{
	public static boolean USE_GREAT_ELLIPTIC_DISTANCE = true;
	protected List<Pair<Point>> linkPairsList;
	protected Polygon small, large;

	public class Pair<T>{
		public final T a,b;

		public Pair(T a, T b) {
			super();
			this.a = a;
			this.b = b;
		}
		
		public String toString() {
			return a +" - "+b;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (a == null) {
				if (other.a != null)
					return false;
			} else if (!a.equals(other.a))
				return false;
			if (b == null) {
				if (other.b != null)
					return false;
			} else if (!b.equals(other.b))
				return false;
			return true;
		}

		
	}
	/**
	 * @param X
	 * @param Y
	 *@author sherif
	 */
	LinkFinder(Polygon X, Polygon Y){
		linkPairsList = new ArrayList<Pair<Point>>();
		if(X.points.size() < Y.points.size()){ 
			small = X;
			large = Y;
		}else{
			small = Y;
			large = X;
		}
	}


	public List<Pair<Point>> getlinkPairsList() {
		if(linkPairsList.isEmpty()){
			// compute the fair capacity for each of the small polygon points
			int fairCapacity = (int) Math.ceil((double)large.points.size()/(double) small.points.size());
			for(Point s : small.points){
				int fairCount = 0;
				// get sorted set of all near by points
				TreeMap<Double, Point> nearestPoints = getSortedNearestPoints(s, large); 
				// add fairCapacity times of the nearby point to the linkPairsList
				for(Entry<Double, Point> e: nearestPoints.entrySet()){
					Point l = e.getValue();
					linkPairsList.add(new Pair<Point>(l,s));
					fairCount++;
					// if the fair capacity reached the go to the next point
					if(fairCount == fairCapacity)
						break;
				}
			}
		}
		return linkPairsList;
	}

	TreeMap<Double, Point> getSortedNearestPoints(Point x, Polygon Y){
		TreeMap<Double, Point> result = new TreeMap<Double, Point>();
		for(Point y : Y.points){
			result.put(distance(x, y), y);
		}
		return result;
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

		LinkFinder lf = new LinkFinder(A, B);
		for(Pair<Point> p : lf.getlinkPairsList()){
			System.out.println(p.a.label + "<-->" + p.b.label);
		}
	}

	public static void main(String args[]) {
		test();
	}
}
