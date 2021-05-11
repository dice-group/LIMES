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
package org.aksw.limes.core.measures.mapper.pointsets;


import org.aksw.limes.core.measures.measure.space.GeoGreatEllipticMeasure;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreatEllipticDistanceTest {

    private static final Logger logger = LoggerFactory.getLogger(GreatEllipticDistanceTest.class);
    @Test
    public void test() {
        System.out
                .println("Distance between Tokyo (35.765278, 140.385556) and San Francisco (37.618889, -122.618889):");

        double ortho = OrthodromicDistance.getDistanceInDegrees(35.765278, 140.385556, 37.618889, -122.618889);
        logger.info("{}","Orthodromic Distance:\t" + ortho + " km");

        double eliptic = GeoGreatEllipticMeasure.getDistanceInDegrees(35.765278, 140.385556, 37.618889, -122.618889);
        logger.info("{}","Elliptic Distance:\t" + eliptic + " km");

        // double geodesic = Geodesic.WGS84.Inverse(35.765278, 140.385556,
        // 37.618889, -122.618889).s12/1000;
        // logger.info("{}","From Geodesic lib:\t" + geodesic + " km");

        logger.info("{}","Diff:\t" + (eliptic - ortho) + " km");
    }

}
