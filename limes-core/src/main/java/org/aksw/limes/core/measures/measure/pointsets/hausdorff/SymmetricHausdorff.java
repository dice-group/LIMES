/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

/**
 *
 * @author ngonga
 */
public class SymmetricHausdorff extends NaiveHausdorff {

    @Override
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
	NaiveHausdorff nh = new NaiveHausdorff();
	return Math.max(nh.computeDistance(X, Y, threshold), nh.computeDistance(Y, X, threshold));
    }

    @Override
    public String getName() {
	return "symmetricHausdorff";
    }
    
    public double getRuntimeApproximation(double mappingSize) {
	return mappingSize / 1000d;
    }
}
