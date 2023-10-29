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

import org.aksw.limes.core.datastrutures.Point;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author psmeros
 */
public class Polygon {

    public List<Point> points;
    public String uri;

    public Polygon(String name) {
        uri = name;
        points = new ArrayList<Point>();
    }

    public Polygon(String name, List<Point> p) {
        uri = name;
        points = p;
    }

    public Polygon(Polygon polygon) {
        uri = polygon.uri;
        points = new ArrayList<Point>(polygon.points);
    }

    /**
     * Adds a point to the polygon. Also updates the distance list
     *
     * @param y
     *            Point to add
     */
    public void add(Point y) {
        points.add(y);
    }

    /**
     * removes a point from the polygon. Also updates the distance list
     *
     * @param y
     *            Point to remove
     */
    public void remove(Point y) {
        ((List<Point>) points).remove(y);
    }

    /**
     * String representation of the polygon
     *
     * @return polygon, as a string
     */
    public String toString() {
        return "\nPolygon " + uri + " " + points.toString();
    }

    /**
     * Return the size of the polygon
     *
     * @return size, the number of points in the polygon
     */
    public long size() {
        return points.size();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GeoSquare))
            return false;

        Polygon o = (Polygon) obj;
        return points.equals(o.points);
    }

    /**
     * Returns a Geometry from the Points of the Polygon
     *
     * @return Geometry
     * @throws org.locationtech.jts.io.ParseException
     *             if Geometry is not valid
     */
    public Geometry getGeometry() throws ParseException {
        String geometryString = "Polygon ((";

        for (Point p : points) {
            geometryString += p.coordinates.get(0);
            geometryString += " ";
            geometryString += p.coordinates.get(1);
            geometryString += ", ";
        }
        geometryString = geometryString.substring(0, geometryString.length() - 2);
        geometryString += "))";

        WKTReader wktReader = new WKTReader();
        return wktReader.read(geometryString);
    }

}
