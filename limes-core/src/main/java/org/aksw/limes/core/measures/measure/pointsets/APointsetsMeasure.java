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
package org.aksw.limes.core.measures.measure.pointsets;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.space.GeoGreatEllipticMeasure;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public abstract class APointsetsMeasure extends AMeasure implements IPointsetsMeasure {

    public static boolean USE_GREAT_ELLIPTIC_DISTANCE = false;
    protected static int computations;

    /**
     * @param x
     *            Point x
     * @param y
     *            Point y
     * @return Point-to-point distance between x and y
     */
    public static double pointToPointDistance(Point x, Point y) {
        computations++;
        if (USE_GREAT_ELLIPTIC_DISTANCE) {
            return GeoGreatEllipticMeasure.getDistanceInDegrees(x, y);
        }
        return OrthodromicDistance.getDistanceInDegrees(x, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(java.lang.
     * Object, java.lang.Object)
     */
    public double getSimilarity(Object object1, Object object2) {
        Polygon p1 = OrchidMapper.getPolygon((String) object1);
        Polygon p2 = OrchidMapper.getPolygon((String) object2);
        double d = computeDistance(p1, p2, 0f);
        return 1d / (1d + (double) d);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure#
     * getComputations()
     */
    public int getComputations() {
        return computations;
    }

}
