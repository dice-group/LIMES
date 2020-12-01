/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * Implements the polygon modifier abstract class. It is responsible for
 * modifying a set of polygons.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public abstract class AbstractPolygonModifier implements IPolygonModifier {

    /* (non-Javadoc)
     * @see org.aksw.limes.core.measures.measure.pointsets.benchmarking.PolygonModifier#modifySet(java.util.Set, double)
     */
    public Set<Polygon> modifySet(Set<Polygon> dataset, double threshold) {
        Set<Polygon> polygons = new HashSet<Polygon>();
        for (Polygon p : dataset) {
            polygons.add(modify(p, threshold));
        }
        return polygons;
    }
}
