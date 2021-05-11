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
package org.aksw.limes.core.ml.algorithm.dragon;

import java.util.HashMap;
import java.util.Map;

public class TrainingInstance {
    private String sourceUri;
    private String targetUri;
    private double classLabel;
    private Map<String, Double> measureValues;

    public TrainingInstance(String sourceUri, String targetUri, double classLabel) {
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
        this.classLabel = classLabel;
        this.measureValues = new HashMap<>();
    }

    @Override
    public String toString() {
        return this.sourceUri + " -> " + this.targetUri + " : " + this.classLabel;
    }

    public String getSourceUri() {
        return this.sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public String getTargetUri() {
        return this.targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public double getClassLabel() {
        return this.classLabel;
    }

    public void setClassLabel(double classLabel) {
        this.classLabel = classLabel;
    }

    public Map<String, Double> getMeasureValues() {
        return this.measureValues;
    }

    public void setMeasureValues(Map<String, Double> measureValues) {
        this.measureValues = measureValues;
    }

}
