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
package org.aksw.limes.core.measures.measure.space;

/**
 * Implements a similarity measure based on the euclidean distance. A Minkowski
 * measure with p=1.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class EuclideanMeasure extends AMinkowskiMeasure {

    @Override
    double outerPTerm(double sum) {
        return Math.sqrt(sum);
    }

    @Override
    double innerPTerm(String xi, String yi) {
        double d = new Double(xi) - new Double(yi);
        return d * d;
    }

    @Override
    public String getName() {
        return "euclidean";
    }

    /**
     * Return the threshold for a dimension. This is used for blocking. Given
     * that the Euclidean metric does not squeeze space as the Mahalanobis does,
     * we simply return the simThreshold
     *
     * @param dimension
     * @param simThreshold
     */
    public double getThreshold(int dimension, double simThreshold) {
        return (1 - simThreshold) / simThreshold;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
