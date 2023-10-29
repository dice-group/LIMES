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

import java.util.HashSet;
import java.util.Set;

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
