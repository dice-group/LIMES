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

public class RefinementNode implements Comparable<RefinementNode> {

    private double fMeasure = -Double.MAX_VALUE;
    protected AMapping map = MappingFactory.createDefaultMapping();
    protected String metricExpression = "";

    public RefinementNode(double fMeasure, AMapping map, String metricExpression) {
        this.setfMeasure(fMeasure);
        this.setMap(map);
        this.setMetricExpression(metricExpression);
    }

    @Override
    public int compareTo(RefinementNode o) {
        return (int) (fMeasure - o.getFMeasure());
    }

    public double getFMeasure() {
        return fMeasure;
    }

    public AMapping getMapping() {
        return map;
    }

    public String getMetricExpression() {
        return metricExpression;
    }

    public void setMetricExpression(String metricExpression) {
        this.metricExpression = metricExpression;
    }

    public void setfMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public void setMap(AMapping map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return getMetricExpression() + " (F = " + getFMeasure() + ")";
    }
}
