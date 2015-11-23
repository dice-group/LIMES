/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

<<<<<<< HEAD

=======
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.GreatEllipticDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
>>>>>>> 04f229403216e5956dd16f2b2e0519c2b5ae47d3
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
}
