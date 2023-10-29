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
package org.aksw.limes.core.ml.algorithm.dragon.Utils;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.dragon.TrainingInstance;

import java.util.List;

public class InstanceCalculator {

    /**
     * @param instanceList
     * @return [positive][negative]
     */
    public static double[] getNumberOfPositiveNegativeInstances(List<TrainingInstance> instanceList) {
        double[] posNegNumber = {0.0, 0.0};
        for (TrainingInstance t : instanceList) {
            if (t.getClassLabel() > 0.9) {
                posNegNumber[0]++;
            } else {
                posNegNumber[1]++;
            }
        }
        return posNegNumber;
    }

    /**
     *
     * @param m
     * @return [positive][negative]
     */
    public static double[] getNumberOfPositiveNegativeInstances(AMapping m) {
        double[] posNegNumber = { 0.0, 0.0 };
        for (String s : m.getMap().keySet()) {
            for (String t : m.getMap().get(s).keySet()) {
                double value = m.getMap().get(s).get(t);
                if (value > 0.9) {
                    posNegNumber[0]++;
                } else {
                    posNegNumber[1]++;
                }
            }
        }
        return posNegNumber;
    }
}
