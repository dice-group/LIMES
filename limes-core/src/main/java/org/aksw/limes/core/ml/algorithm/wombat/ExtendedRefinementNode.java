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
package org.aksw.limes.core.ml.algorithm.wombat;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;

/**
 *
 */
public class ExtendedRefinementNode extends RefinementNode {

    private double maxFMeasure = 1d;

    public ExtendedRefinementNode() {
        super(-Double.MAX_VALUE, MappingFactory.createDefaultMapping(), "");
    }


    public ExtendedRefinementNode(double fMeasure, AMapping map, String metricExpression) {
        super(fMeasure, map , metricExpression);
    }

    public ExtendedRefinementNode(double fMeasure, AMapping map, String metricExpression, AMapping refMap, double beta, double rMax) {
        super(fMeasure, map , metricExpression);
        double pMax = computeMaxPrecision(map, refMap);
        maxFMeasure = (1+Math.pow(beta,2)) * pMax * rMax / (Math.pow(beta,2) * pMax + rMax);
    }

    private double computeMaxPrecision(AMapping map, AMapping refMap) {
        AMapping falsePos = MappingFactory.createDefaultMapping();
        for (String key : map.getMap().keySet()) {
            for (String value : map.getMap().get(key).keySet()) {
                if (refMap.getMap().containsKey(key) || refMap.getReversedMap().containsKey(value)) {
                    falsePos.add(key, value, map.getMap().get(key).get(value));
                }
            }
        }
        AMapping m = MappingOperations.difference(falsePos, refMap);
        return (double) refMap.size() / (double) (refMap.size() + m.size());
    }

    /**
     * @return max F-Score
     * @author sherif
     */
    public double getMaxFMeasure() {
        return maxFMeasure;
    }

    public void setMaxFMeasure(double maxFMeasure) {
        this.maxFMeasure = maxFMeasure;
    }


}
