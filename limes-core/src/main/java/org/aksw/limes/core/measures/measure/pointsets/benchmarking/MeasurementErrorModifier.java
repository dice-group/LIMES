/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class MeasurementErrorModifier extends AbstractPolygonModifier {

    /**
     * Modifies a polygon by adding a random error between -threshold and
     * +threshold to its latitude and longitude
     *
     * @param p
     *         Polygon to modify
     * @param threshold
     *         Error range
     * @return Modified polygon with the same name
     */
    public Polygon modify(Polygon p, double threshold) {
        Polygon q = new Polygon(p.uri);
        List<Point> points = new ArrayList<Point>();
        for (Point point : p.points) {
            List<Double> coordinates = new ArrayList<Double>();
            for (Double f : point.coordinates) {
                double v;
                double delta = (double) (Math.random() * threshold);
                if (Math.random() <= 0.5) {
                    v = f + delta;
                } else {
                    v = f - delta; // deal with lat and long issue
                }
                if (Math.abs(f) <= 90 && Math.abs(v) > 90) {
                    coordinates.add(Math.signum(v) * (180 - Math.abs(v)));
                }
                if (Math.abs(f) <= 180 && Math.abs(v) > 180) {
                    coordinates.add(Math.signum(v) * (360 - Math.abs(v)));
                } else {
                    coordinates.add(v);
                }
            }
            points.add(new Point(point.label, coordinates));
        }
        q.points = points;
        return q;
    }

    public String getName() {
        return "MeasurementErrorModifier";
    }

}