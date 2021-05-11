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
package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The class implements the evaluate method to evaluate mappings against several qualitative measures
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class QualitativeMeasuresEvaluator {
    static Logger logger = LoggerFactory.getLogger(QualitativeMeasuresEvaluator.class);


    Map<EvaluatorType, Double> evaluations = new LinkedHashMap<EvaluatorType, Double>(); //new HashMap<EvaluatorType, Double>();


    /**
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @param evaluationMeasures It is the set of qualitative measures to evaluate the predicted mappings
     * @return Map - It contains the measure name and the corresponding calculated value
     */
    public Map<EvaluatorType, Double> evaluate(AMapping predictions, GoldStandard goldStandard, Set<EvaluatorType> evaluationMeasures) {
        for (EvaluatorType measureType : evaluationMeasures) {

            IQualitativeMeasure measure = EvaluatorFactory.create(measureType);
            double evaluationValue = measure.calculate(predictions, goldStandard);
            evaluations.put(measureType, evaluationValue);
        }

        return evaluations;
    }
}