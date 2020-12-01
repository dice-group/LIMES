/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;

import java.util.Set;

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * Implements the polygon modifier interface. It provides basic functions for
 * modifying a set of polygons.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public interface IPolygonModifier {
    /**
     * Modifies a set of polygons give a threshold
     *
     * @param dataset,
     *         set of polygons
     * @param threshold,
     *         the threshold
     * @return set of polygons, modified
     */
    Set<Polygon> modifySet(Set<Polygon> dataset, double threshold);

    /**
     * Modifies a polygon given a threshold.
     *
     * @param polygon to be modified
     * @param threshold of modification
     * @return modified polygon
     */
    Polygon modify(Polygon polygon, double threshold);

    /**
     * Return name of modifier class
     *
     * @return name of modifier, as string
     */
    String getName();
}
