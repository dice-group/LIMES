/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets;

import java.util.Set;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.measure.Measure;

/**
 *
 * @author ngonga
 */
public interface SetMeasure extends Measure {

    public static boolean USE_GREAT_ELLIPTIC_DISTANCE = true; // if false
							      // orthodomic
							      // distance will
							      // be used

    public double computeDistance(Polygon X, Polygon Y, double threshold);

    public Mapping run(Set<Polygon> source, Set<Polygon> target, double threshold);

    public int getComputations();

    public String getName();
}
