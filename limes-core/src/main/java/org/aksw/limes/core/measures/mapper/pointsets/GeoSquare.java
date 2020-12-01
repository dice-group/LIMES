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
