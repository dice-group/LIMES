/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;

import java.util.Set;

import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

/**
 *
 * @author ngonga
 */
public interface PolygonModifier {
    Set<Polygon> modifySet(Set<Polygon> dataset, double threshold);
    Polygon modify(Polygon p, double threshold);
	/**
	 * @return
	 * @author sherif
	 */
	String getName();
}
