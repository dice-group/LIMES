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
package org.aksw.limes.core.evaluation.evaluator;

import org.aksw.limes.core.evaluation.qualititativeMeasures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Factory class gives a measure object based on the specified measure type.<br>
 * This object can be used through its method calculate() to evaluate the generated mappings
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class EvaluatorFactory {
    static Logger logger = LoggerFactory.getLogger(EvaluatorFactory.class);


    public static IQualitativeMeasure create(EvaluatorType measure) {
        if (measure == EvaluatorType.PRECISION)
            return new Precision();
        else if (measure == EvaluatorType.RECALL)
            return new Recall();
        else if (measure == EvaluatorType.F_MEASURE)
            return new FMeasure();
        else if (measure == EvaluatorType.P_PRECISION)
            return new PseudoPrecision();
        else if (measure == EvaluatorType.P_RECALL)
            return new PseudoRecall();
        else if (measure == EvaluatorType.PF_MEASURE)
            return new PseudoFMeasure();
        else if (measure == EvaluatorType.PR_PRECISION)
            return new PseudoRefPrecision();
        else if (measure == EvaluatorType.PR_RECALL)
            return new PseudoRefRecall();
        else if (measure == EvaluatorType.PRF_MEASURE)
            return new PseudoRefFMeasure();
        else if (measure == EvaluatorType.ACCURACY)
            return new Accuracy();


        return null;
    }
}
