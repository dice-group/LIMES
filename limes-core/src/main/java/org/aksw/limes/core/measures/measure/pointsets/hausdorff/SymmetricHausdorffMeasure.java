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

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class SymmetricHausdorffMeasure extends NaiveHausdorffMeasure {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff#
     * computeDistance(org.aksw.limes.core.measures.mapper.atomic.hausdorff.
     * Polygon, org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon,
     * double)
     */
    @Override
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        NaiveHausdorffMeasure nh = new NaiveHausdorffMeasure();
        return Math.max(nh.computeDistance(X, Y, threshold), nh.computeDistance(Y, X, threshold));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff#
     * getName()
     */
    @Override
    public String getName() {
        return "symmetricHausdorff";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff#
     * getRuntimeApproximation(double)
     */
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
