/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
