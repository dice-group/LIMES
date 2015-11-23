/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic.hausdorff;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.aksw.limes.core.datastrutures.Point;

/**
 *
 * @author ngonga
 * @author psmeros
 */
public class Polygon {

    public List<Point> points;
    public String uri;

    /**
     * Creates a new Polygon
     */
    public Polygon(String name) {
        uri = name;
        points = new ArrayList<Point>();
    }
    
    public Polygon(String name, List<Point> p) {
        uri = name;
        points = p;
    }
    
    // copy constructor
    public Polygon(Polygon polygon) {
        uri = polygon.uri;
        points = new ArrayList<Point>(polygon.points);
    }

    /**
     * Adds a point to the polygon. Also updates the distance list
     *
     * @param y Point to add
     */
    public void add(Point y) {
        points.add(y);
    }
    
    /**
     * removes a point from the polygon. Also updates the distance list
     *
     * @param y Point to remove
     */
    public void remove(Point y) {
    	((List<Point>)points).remove(y);
    }
    
    
    public String toString()
    {
//        return "Polygon "+uri;
        return "\nPolygon " + uri + " " + points.toString();
    }
    
    public long size(){
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
     * @return Geometry
     * @throws com.vividsolutions.jts.io.ParseException
     */
    public Geometry getGeometry() throws ParseException {
        String geometryString = "Polygon ((";
        
        for (Point p: points) {
            geometryString += p.coordinates.get(0);
            geometryString += " ";
            geometryString += p.coordinates.get(1);
            geometryString += ", ";
        }
        geometryString = geometryString.substring(0, geometryString.length()-2);
        geometryString += "))";
        
        WKTReader wktReader = new WKTReader();
        return wktReader.read(geometryString);
    }
        
}
