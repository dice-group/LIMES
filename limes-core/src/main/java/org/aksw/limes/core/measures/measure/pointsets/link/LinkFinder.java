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
package org.aksw.limes.core.measures.measure.pointsets.link;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.APointsetsMeasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author sherif class to generate the link pairs between 2 polygons
 */
public class LinkFinder {
    protected List<PairSimilar<Point>> linkPairsList;
    protected Polygon small, large;

    /**
     * Constructor
     *
     * @param X Polygon
     * @param Y Polygon
     */
    public LinkFinder(Polygon X, Polygon Y) {
        linkPairsList = new ArrayList<PairSimilar<Point>>();
        if (X.points.size() < Y.points.size()) {
            small = X;
            large = Y;
        } else {
            small = Y;
            large = X;
        }
    }

    /**
     * @return list of link pairs
     */
    public List<PairSimilar<Point>> getlinkPairsList() {
        if (linkPairsList.isEmpty()) {
            // compute the fair capacity for each of the small polygon points
            int fairCapacity = (int) Math.ceil((double) large.points.size() / (double) small.points.size());
            for (Point s : small.points) {
                int fairCount = 0;
                // get sorted set of all near by points
                TreeMap<Double, Point> nearestPoints = getSortedNearestPoints(s, large);
                // add fairCapacity times of the nearby point to the
                // linkPairsList
                for (Entry<Double, Point> e : nearestPoints.entrySet()) {
                    Point l = e.getValue();
                    linkPairsList.add(new PairSimilar<Point>(l, s));
                    fairCount++;
                    // if the fair capacity reached the go to the next point
                    if (fairCount == fairCapacity)
                        break;
                }
            }
        }
        return linkPairsList;
    }

    /**
     * @param x Point
     * @param Y Point
     * @return Sorted nearest points
     */
    TreeMap<Double, Point> getSortedNearestPoints(Point x, Polygon Y) {
        TreeMap<Double, Point> result = new TreeMap<Double, Point>();
        for (Point y : Y.points) {
            result.put(APointsetsMeasure.pointToPointDistance(x, y), y);
        }
        return result;
    }

    /**
     * @param mappingSize mapping size
     * @return run time approximation
     */
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}
