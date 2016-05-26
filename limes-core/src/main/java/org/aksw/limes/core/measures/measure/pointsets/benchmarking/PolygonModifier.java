/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

import java.util.Set;

/**
 * Implements the polygon modifier interface. It provides basic functions for
 * modifying a set of polygons.
 *
 * @author ngonga
 */
public interface PolygonModifier {
    /**
     * Modifies a set of polygons give a threshold
     *
     * @param dataset,
     *         set of polygons
     * @param threshold,
     *         the threshold
     * @return set of polygons, modified
     * @author ngonga
     */
    Set<Polygon> modifySet(Set<Polygon> dataset, double threshold);

    /**
     * Modifies a polygon given a threshold.
     *
     * @param polygon,
     *         the polygon
     * @param threshold,
     *         the threshold
     * @return a polygon, modified
     * @author ngonga
     */
    Polygon modify(Polygon p, double threshold);

    /**
     * Return name of modifier class
     *
     * @return name of modifier, as string
     */
    String getName();
}
