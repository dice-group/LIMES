/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

/**
 *
 * @author ngonga
 */
public class FastHausdorff extends NaiveHausdorff {

    /**
     * Efficient computation of the Hausdorff distance between two polygons
     *
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @return Distance between the two polygons
     */
    @Override
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
	double max = 0f;
	double d;
	double min;
	for (Point x : X.points) {
	    min = Float.POSITIVE_INFINITY;
	    for (Point y : Y.points) {
		d = distance(x, y);
		if (min > d) {
		    min = d;
		}
	    }
	    if (min > threshold) {
		return min;
	    }
	    if (max < min) {
		max = min;
	    }
	}
	return max;
    }

    public FastHausdorff() {
	computations = 0;
    }

    public String getName() {
	return "fast";
    }
}
