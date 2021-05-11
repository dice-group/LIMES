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
package org.aksw.limes.core.datastrutures;

import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;

import java.util.List;
import java.util.Map;

/**
 * This class combines a machine learning algorithm information to be executed
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */

public class TaskAlgorithm {
    /** The implementation type of the machine learning algorithm such as unsupervised, supervised batch and supervised active */
    private MLImplementationType mlType;
    /** The machine learning algorithm method such as EAGLE, WOMBAT and  LION */
    private AMLAlgorithm mlAlgorithm;
    /** The parameters required by the machine learning algorithm*/
    private List<LearningParameter> mlParameter;
    /** parameter values to explore in grid search */
    private Map<LearningParameter, List<Object>> mlParameterValues;

    private String name;

    public TaskAlgorithm() {
    }

    public TaskAlgorithm(MLImplementationType mlType,AMLAlgorithm mlAlgorithm,List<LearningParameter> mlParameter) {
        this.mlType = mlType;
        this.mlAlgorithm=mlAlgorithm;
        this.mlParameter=mlParameter;
    }

    public TaskAlgorithm(MLImplementationType mlType, AMLAlgorithm mlAlgorithm, List<LearningParameter> mlParameter,
                         Map<LearningParameter, List<Object>> mlParameterValues) {
        this.mlType = mlType;
        this.mlAlgorithm = mlAlgorithm;
        this.mlParameter = mlParameter;
        this.mlParameterValues = mlParameterValues;
    }

    public MLImplementationType getMlType() {
        return mlType;
    }

    public void setMlType(MLImplementationType mlType) {
        this.mlType = mlType;
    }

    public AMLAlgorithm getMlAlgorithm() {
        return mlAlgorithm;
    }

    public void setMlAlgorithm(AMLAlgorithm mlAlgorithm) {
        this.mlAlgorithm = mlAlgorithm;
    }

    public List<LearningParameter> getMlParameter() {
        return mlParameter;
    }

    public void setMlParameter(List<LearningParameter> mlParameter) {
        this.mlParameter = mlParameter;
    }

    public Map<LearningParameter, List<Object>> getMlParameterValues() {
        return mlParameterValues;
    }

    public void setMlParameterValues(Map<LearningParameter, List<Object>> mlParameterValues) {
        this.mlParameterValues = mlParameterValues;
    }

    public String getName() {
        if (name == null || name.equals("")) {
            return mlAlgorithm.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}