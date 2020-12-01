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
public class GranularityModifier extends AbstractPolygonModifier {

    /**
     * Modifies a polygon by reducing the total amount of points that describe
     * it to threshold * original number of points. Assumes that the threshold
     * is less than 1. If it larger than 1, then it is replaced by 1/threshold.
     * The reduction is carried out randomly by "flipping a coin".
     *
     * @param p Polygon
     * @param threshold of distance
     * @return polygon, the modified polygon
     */
    public Polygon modify(Polygon p, double threshold) {
        if (threshold > 1) {
            threshold = 1d / threshold;
        }
        Polygon q = new Polygon(p.uri);
        // ensure that we have at least one point
        List<Point> points = new ArrayList<Point>();
        points.add(p.points.get(0));

        // rest is added probabilistically
        for (int i = 1; i < p.points.size(); i++) {
            if (Math.random() <= threshold) {
                points.add(p.points.get(i));
            }
        }
        q.points = points;
        return q;
    }

    public String getName() {
        return "GranularityModifier";
    }

}
