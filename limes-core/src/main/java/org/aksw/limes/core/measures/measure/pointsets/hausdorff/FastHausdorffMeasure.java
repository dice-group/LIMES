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
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * Efficient computation of the Hausdorff distance between two polygons
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class FastHausdorffMeasure extends NaiveHausdorffMeasure {

    /**
     * Constructor
     */
    public FastHausdorffMeasure() {
        computations = 0;
    }

    /**
     *
     * @param X
     *            First polygon
     * @param Y
     *            Second polygon
     * @return Distance between the two polygons
     */
    @Override
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        double max = 0f;
        double d;
        double min;
        for (Point x : X.points) {
            min = Float.POSITIVE_INFINITY;
            for (Point y : Y.points) {
                d = pointToPointDistance(x, y);
                if (min > d) {
                    min = d;
                }
            }
            if (min > threshold) {
                return min;
            }
            if (max < min) {
                max = min;
            }
        }
        return max;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff#
     * getName()
     */
    public String getName() {
        return "fast";
    }
}
