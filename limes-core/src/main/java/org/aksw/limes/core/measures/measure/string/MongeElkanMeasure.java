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
package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;

public class MongeElkanMeasure extends StringMeasure {

    public static final String split = " ";

    public double proximity(String s1, String s2) {
        String[] sourceToken = s1.split(split);
        String[] targetToken = s2.split(split);
        double simB = 0d;
        double result = 0d;
        TrigramMeasure internMeasure = new TrigramMeasure();
        for (String sourceString : sourceToken) {
            double maxSim = 0d;
            for (String targetString : targetToken) {
                double sim = internMeasure.getSimilarity(sourceString, targetString);
                if (maxSim < sim) {
                    maxSim = sim;
                }
                if (maxSim == 1d) {
                    break;
                }
            }
            simB += maxSim;
        }
        if (simB != 0) {
            result = simB / sourceToken.length;
        }
        return result;
    }

    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean computableViaOverlap() {
        return false;
    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        return proximity(object1.toString(), object2.toString());
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double max = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = proximity(source, target);
                if (sim > max) {
                    max = sim;
                }
            }
        }
        return max;
    }

    @Override
    public String getName() {
        return "ratcliff-obershelp";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}