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
 * Implements a similarity measure based on the Manhattan distance. A Minkowski
 * measure with p=1.
 *
 * @author Jan Lippert (lippertsjan@gmail.com)
 */
public class ManhattanMeasure extends AMinkowskiMeasure {

    @Override
    double outerPTerm(double sum) {
        return sum;
    }

    @Override
    double innerPTerm(String xi, String yi) {
        return Math.abs(new Double(xi) - new Double(yi));
    }

    @Override
    public String getName() {
        return "manhattan";
    }

    @Override
    public double getThreshold(int dimension, double simThreshold) {
        return (1 - simThreshold) / simThreshold;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
