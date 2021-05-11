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
package org.aksw.limes.core.measures.mapper.pointsets;

import java.util.HashSet;
import java.util.Set;

/**
 * Same as a hypercube for polygons
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class GeoSquare {

    public Set<Polygon> elements;

    public GeoSquare() {
        elements = new HashSet<Polygon>();
    }

    /**
     * String representation of elements.
     *
     * @return elements, as a string
     */
    public String toString() {
        return elements.toString();
    }

    /**
     * Return the size of the polygon
     *
     * @return size, the number of elements in the polygon
     */
    public long size() {
        long size = 0;
        for (Polygon p : elements) {
            size += p.size();
        }
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GeoSquare))
            return false;

        GeoSquare o = (GeoSquare) obj;
        return elements.equals(o.elements);
    }

}
