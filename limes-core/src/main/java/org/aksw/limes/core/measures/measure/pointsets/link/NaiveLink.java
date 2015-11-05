/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.link;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
import org.aksw.limes.core.measures.measure.pointsets.link.LinkFinder.Pair;

/**
 * @author sherif
 *
 */
public class NaiveLink implements SetMeasure{

	public int computations;
	
	
	/**
	 * Approach to computing the link distance between two
	 * polygons
	 *
	 * @param X First polygon
	 * @param Y Second polygon
	 * @return Distance between the two polygons
	 */
	public NaiveLink() {
		computations = 0;
	}

	public int getComputations() {
		return computations;
	}

	public double computeDistance(Polygon X, Polygon Y, double threshold) {
		double sum = 0;
		LinkFinder fsf = new LinkFinder(X, Y);
		fsf.USE_GREAT_ELLIPTIC_DISTANCE = this.USE_GREAT_ELLIPTIC_DISTANCE;

		for(Pair<Point> p : fsf.getlinkPairsList()){
			sum += distance(p.a, p.b);
		}
		return sum;
	}

	public static double distance(Polygon X, Polygon Y, double threshold) {
		double sum = 0;
		LinkFinder fsf = new LinkFinder(X, Y);

		for(Pair<Point> p : fsf.getlinkPairsList()){
			sum = (new NaiveLink()).distance( p.a, p.b);
		}
		return sum;
	}


	
	
	public String getName() {
		return "naiveLink";
	}

	/**
	 * Computes the SetMeasure distance for a source and target set
	 *
	 * @param source Source polygons
	 * @param target Target polygons
	 * @param threshold Distance threshold
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
     * @param x Point x
     * @param y Point y
     * @return Distance between x and y
     */
    public double distance(Point x, Point y) {
        computations++;
        if(USE_GREAT_ELLIPTIC_DISTANCE){
        	return GreatEllipticDistance.getDistanceInDegrees(x,y);
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
		return 1d/(1d + (double)d);
	}

	public double getRuntimeApproximation(double mappingSize) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static void test() {
		//Malta in DBpedia
		Point maltaDbpediaP1 = new Point("MaltaDbpediaP1", Arrays.asList(new Double[]{14.4625, 35.8967}));
		Point maltaDbpediaP2 = new Point("MaltaDbpediaP2", Arrays.asList(new Double[]{14.4625, 35.8833}));
		Point maltaDbpediaP3 = new Point("MaltaDbpediaP3", Arrays.asList(new Double[]{14.5, 35.8833}));
		Point maltaDbpediaP4 = new Point("MaltaDbpediaP4", Arrays.asList(new Double[]{14.5, 35.8967}));
		Polygon maltaDbpediaPoly1 = new Polygon("maltaDbpediaPoly1", Arrays.asList(new Point[]{maltaDbpediaP1, maltaDbpediaP2, maltaDbpediaP3, maltaDbpediaP4}));
		Set<Polygon> maltaDbpedia = new HashSet<Polygon>();
		maltaDbpedia.add(maltaDbpediaPoly1);

		//Malta in Nuts
		Point maltaNutsP1 = new Point("MaltaNutsP1", Arrays.asList(new Double[]{14.342771550000066, 35.931038250000043}));
		Point maltaNutsP2 = new Point("MaltaNutsP2", Arrays.asList(new Double[]{14.328761050000054, 35.990215250000048}));
		Point maltaNutsP3 = new Point("MaltaNutsP3", Arrays.asList(new Double[]{14.389599050000101, 35.957935750000019}));
		Point maltaNutsP4 = new Point("MaltaNutsP4", Arrays.asList(new Double[]{14.56211105, 35.819926750000036}));
		Point maltaNutsP5 = new Point("MaltaNutsP5", Arrays.asList(new Double[]{14.416516550000068, 35.828308250000049}));
		Point maltaNutsP6 = new Point("maltaNutsP6", Arrays.asList(new Double[]{14.212639050000092, 36.07996375}));
		Point maltaNutsP7 = new Point("maltaNutsP7", Arrays.asList(new Double[]{14.336017550000065, 36.032375750000057}));
		Point maltaNutsP8 = new Point("maltaNutsP8", Arrays.asList(new Double[]{14.218683050000095, 36.021091250000026}));
		Point maltaNutsP9 = new Point("maltaNutsP9", Arrays.asList(new Double[]{14.18619805000003, 36.036388750000029}));
		Polygon maltaNutsPoly1 = new Polygon("maltaNutsPoly1", Arrays.asList(new Point[]{maltaNutsP1, maltaNutsP2, maltaNutsP3, maltaNutsP4, maltaNutsP5
				, maltaNutsP6, maltaNutsP7, maltaNutsP8, maltaNutsP9}));
		Set<Polygon> maltaNuts = new HashSet<Polygon>();
		maltaNuts.add(maltaNutsPoly1);

		//Malta in LGD
		Point maltaLgdP1 = new Point("maltaLgdP1", Arrays.asList(new Double[]{14.504285, 35.8953019}));
		Polygon maltaLgdPoly1 = new Polygon("maltaLgdPoly1", Arrays.asList(new Point[]{maltaLgdP1}));
		Set<Polygon> maltaLgd = new HashSet<Polygon>();
		maltaLgd.add(maltaLgdPoly1);

		NaiveLink link = new NaiveLink();
		System.out.println(link.run(maltaNuts, maltaDbpedia, Double.MAX_VALUE));;
	}

	public static void main(String args[]) {
		test();
	}

}
