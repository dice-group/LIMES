/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.max;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.PointsetsMeasure;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author sherif
 *
 */
public class NaiveMax extends PointsetsMeasure {

	public int computations;

	/**
	 * Brute force approach to computing the MAX distance between two polygons
	 *
	 * @param X First polygon
	 * @param Y Second polygon
	 * @return Distance between the two polygons
	 */
	public NaiveMax() {
		computations = 0;
	}

	
	/* (non-Javadoc)
	 * @see org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#computeDistance(org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon, org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon, double)
	 */
	public double computeDistance(Polygon X, Polygon Y, double threshold) {
		double max = 0.0;
		double d;
		for (Point x : X.points) {
			for (Point y : Y.points) {
				d = distance(x, y);
				if (max < d) {
					max = d;
				}
			}
		}
		return max;
	}

	/**
	 * @param X Polygon
	 * @param Y Polygon
	 * @param threshold
	 * @return Max distance between X and Y
	 */
	public static double distance(Polygon X, Polygon Y, double threshold) {
		double max = 0.0;
		double d;
		for (Point x : X.points) {
			for (Point y : Y.points) {
				d = (new NaiveMax()).distance(x, y);
				if (max < d) {
					max = d;
				}
			}

		}
		return max;
	}

	/* (non-Javadoc)
	 * @see org.aksw.limes.core.measures.measure.IMeasure#getName()
	 */
	public String getName() {
		return "naiveMax";
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

	
	/* (non-Javadoc)
	 * @see org.aksw.limes.core.measures.measure.IMeasure#getType()
	 */
	public String getType() {
		return "geodistance";
	}

	/* (non-Javadoc)
	 * @see org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(org.aksw.limes.core.io.cache.Instance, org.aksw.limes.core.io.cache.Instance, java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see org.aksw.limes.core.measures.measure.IMeasure#getRuntimeApproximation(double)
	 */
	public double getRuntimeApproximation(double mappingSize) {
		return mappingSize / 1000d;
	}



}
