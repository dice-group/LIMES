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
/**
 *
 */
package org.aksw.limes.core.measures.measure.pointsets.surjection;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.APointsetsMeasure;

import java.util.ArrayList;
import java.util.List;

/**
 * class to find the surjection of the larger polygon to the smaller one.
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public class SurjectionFinder {
    protected List<PairSimilar<Point>> surjectionPairsList;
    protected Polygon small, large;

    /**
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @author sherif
     */
    SurjectionFinder(Polygon X, Polygon Y) {
        surjectionPairsList = new ArrayList<PairSimilar<Point>>();
        if (X.points.size() < Y.points.size()) {
            small = X;
            large = Y;
        } else {
            small = Y;
            large = X;
        }
    }

    /**
     * @return SurjectionPairsList
     */
    public List<PairSimilar<Point>> getSurjectionPairsList() {
        if (surjectionPairsList.isEmpty()) {
            Polygon largeCopy = new Polygon(large);

            // find nearest points (l) to each point of the small polygon (s)
            // and add the pairs (l,s) to the surjectionPairsList
            for (Point s : small.points) {
                Point l = getNearestPoint(s, largeCopy);
                surjectionPairsList.add(new PairSimilar<Point>(l, s));
                largeCopy.remove(l);
            }

            // for each of the rest points of the large polygon (l)
            // find nearest point (s) from the small polygon
            // and add the pairs (l,s) to the surjectionPairsList
            for (Point l : largeCopy.points) {
                Point s = getNearestPoint(l, small);
                surjectionPairsList.add(new PairSimilar<Point>(l, s));
            }
        }
        return surjectionPairsList;
    }

    /**
     * @param x
     *            Point
     * @param Y
     *            Polygon
     * @return the nearest to x from the points of the polygon Y
     */
    protected Point getNearestPoint(Point x, Polygon Y) {
        double d, min = Double.MAX_VALUE;
        Point result = null;
        for (Point y : Y.points) {
            d = APointsetsMeasure.pointToPointDistance(x, y);
            if (d < min) {
                min = d;
                result = y;
            }
        }
        return result;
    }

    /**
     * @param mappingSize
     * @return
     */
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}
