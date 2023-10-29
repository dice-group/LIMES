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
package org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions;

import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

public abstract class FitnessFunctionDTL {
    protected DecisionTree dt;

    public abstract ExtendedClassifier getBestClassifier(DecisionTree currentNode);

    public abstract boolean stopCondition(DecisionTree currentNode);

    protected PropertyMapping propertyMapping;

    public void setDt(DecisionTree dt){
        this.dt = dt;
    }

    public PropertyMapping getPropertyMapping() {
        return propertyMapping;
    }

    public void setPropertyMapping(PropertyMapping propertyMapping) {
        this.propertyMapping = propertyMapping;
    }

}
