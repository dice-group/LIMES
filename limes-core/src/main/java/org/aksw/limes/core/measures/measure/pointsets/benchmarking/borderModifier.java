/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * In order not to generate a self intersecting modified polygons, in this
 * modifier, for each line segment p1 p2 across the input polygon we generate 2
 * random points within the line segment p1 p2
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public class borderModifier extends AbstractPolygonModifier {

    /**
     * Modifies a polygon by adding a random error between -threshold and
     * +threshold to its latitude and longitude
     *
     * @param poly
     *         Polygon to modify
     * @param threshold
     *         Error range
     * @return Modified polygon with the same name
     */
    public Polygon modify(Polygon poly, double threshold) {
        if (poly.points.size() <= 2) {
            return (new MeasurementErrorModifier()).modify(poly, 1.0);
        }
        Polygon result = new Polygon(poly.uri);
        List<Point> points = new ArrayList<Point>();
        for (int i = 0; i < poly.points.size(); i++) {
            double x1 = poly.points.get(i).coordinates.get(0), y1 = poly.points.get(i).coordinates.get(1),
                    x2 = poly.points.get((i + 1) % poly.points.size()).coordinates.get(0),
                    y2 = poly.points.get((i + 1) % poly.points.size()).coordinates.get(1);

            double t = threshold; // Math.random();
            double x = x1 + (x2 - x1) * t;
            double y = y1 + (y2 - y1) * t;
            List<Double> coordinates = new ArrayList<Double>(Arrays.asList(x, y));
            points.add(new Point(poly.points.get(i).label, coordinates));

        }
        result.points = points;
        return result;
    }

    @Override
    public String getName() {
        return "InLineMeasurementErrorModifier";
    }

}