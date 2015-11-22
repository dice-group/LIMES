/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import org.aksw.limes.core.data.Instance;
import org.aksw.limes.core.data.Point;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure;

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
}
