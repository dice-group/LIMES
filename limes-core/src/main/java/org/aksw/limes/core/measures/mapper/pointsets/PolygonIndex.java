/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.pointsets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class PolygonIndex {

    static Logger logger = LoggerFactory.getLogger(PolygonIndex.class);
    public Map<String, Map<Point, Map<Point, Double>>> distanceIndex;
    public Map<String, Polygon> polygonIndex;
    public int computations;

    public PolygonIndex() {
        distanceIndex = new HashMap<String, Map<Point, Map<Point, Double>>>();
        polygonIndex = new HashMap<String, Polygon>();
        computations = 0;
    }

    /**
     * String representation of the polygon index
     *
     * @return polygon index, as a string
     */
    public String toString() {
        return distanceIndex.toString();
    }

    /**
     * Indexes a list of polygons by mapping the uri of each polygon to the
     * corresponding distanceIndex
     *
     * @param polygons
     *            to be indexed
     */
    public void index(Set<Polygon> polygons) {
        for (Polygon x : polygons) {
            index(x);
        }
    }

    /**
     * Indexes the distances between the points in a given polygon and adds
     * polygon to list of indexes
     *
     * @param p
     *            Input polygon
     */
    public void index(Polygon p) {
        Map<Point, Map<Point, Double>> index = new HashMap<Point, Map<Point, Double>>();
        Map<Point, Double> distances;
        for (int i = 0; i < p.points.size(); i++) {
            distances = new HashMap<Point, Double>();
            for (int j = i + 1; j < p.points.size(); j++) {
                distances.put(p.points.get(j),
                        OrthodromicDistance.getDistanceInDegrees(p.points.get(i), p.points.get(j)));
                computations++;
            }
            // if (!distances.isEmpty()) {
            index.put(p.points.get(i), distances);
            // }
        }
        distanceIndex.put(p.uri, index);
        polygonIndex.put(p.uri, p);
    }

    /**
     * Returns the distances between two points x and y from the polygon with
     * label uri Returns -1 if nothing is found
     *
     * @param uri
     *            Label of the polygon
     * @param x
     *            First point from the polygon
     * @param y
     *            Second point from the polygon
     * @return Distance between x and y
     */
    public double getDistance(String uri, Point x, Point y) {
        if (x.equals(y)) {
            return 0f;
        }
        if (polygonIndex.containsKey(uri)) {
            try {
                if (distanceIndex.get(uri).get(x).containsKey(y)) {
                    return distanceIndex.get(uri).get(x).get(y);
                } else {
                    return distanceIndex.get(uri).get(y).get(x);
                }
                // return distanceIndex.get(uri).get(x).get(y);
            } catch (Exception e) {
                logger.warn("Error for uri" + uri + "\t Index contains uri = " + distanceIndex.containsKey(uri)
                        + "\nx = " + x + "\ty = " + y);
                return OrthodromicDistance.getDistanceInDegrees(x, y);
            }
        } else {
            logger.warn(uri + "\t Index contains uri = " + polygonIndex.containsKey(uri));
            if (distanceIndex.containsKey(uri)) {

                logger.warn(uri + "\t Distance index contains " + x + " = " + distanceIndex.get(uri).containsKey(x));
                logger.warn(uri + "\t Distance index contains " + x + " = " + distanceIndex.get(uri).containsKey(y));
            }
            return OrthodromicDistance.getDistanceInDegrees(x, y);
        }
    }

}
