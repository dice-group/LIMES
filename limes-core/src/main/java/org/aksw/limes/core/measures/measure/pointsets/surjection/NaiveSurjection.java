/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.surjection;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.PointsetsMeasure;
import org.aksw.limes.core.util.Pair;

/**
 * @author sherif
 *
 */
public class NaiveSurjection extends PointsetsMeasure {

	public int computations;

	/**
	 * Approach to computing the Surjection distance between two polygons
	 *
	 * @param X First polygon
	 * @param Y Second polygon
	 * @return Distance between the two polygons
	 */
	public NaiveSurjection() {
		computations = 0;
	}



	public double computeDistance(Polygon X, Polygon Y, double threshold) {
		SurjectionFinder sf = new SurjectionFinder(X, Y);

		double sum = 0;
		for (Pair<Point> p : sf.getSurjectionPairsList()) {
			sum += pointToPointDistance(p.a, p.b);
		}
		return sum;
	}

	public static double pointToPointDistance(Polygon X, Polygon Y, double threshold) {
		double sum = 0;
		SurjectionFinder sf = new SurjectionFinder(X, Y);

		for (Pair<Point> p : sf.getSurjectionPairsList()) {
			sum = pointToPointDistance(p.a, p.b);
		}
		return sum;
	}

	public String getName() {
		return "naiveSurjection";
	}

	/**
	 * Computes the SetMeasure distance for a source and target set
	 *
	 * @param source Source polygons
	 * @param target Target polygons
	 * @param threshold Distance threshold
	 * @return Mapping of uris from source to target
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
