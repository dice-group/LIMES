package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class's function is to evaluate mappings against several qaulitative measures
 *
 * @author mofeed
 * @version 1.0
 */
public class QualitativeMeasuresEvaluator {
    static Logger logger = Logger.getLogger(QualitativeMeasuresEvaluator.class);


    Map<EvaluatorType, Double> evaluations = new HashMap<EvaluatorType, Double>();


    /**
     * @param prediction:
     *         the results predicted to represent mappings between two datasets
     * @param goldStandard:
     *         It is an object that contains {Mapping-> gold standard, List of source URIs, List of target URIs}
     * @param evaluationMeasures:
     *         Set of Measures to evaluate the resulted mappings against
     * @return a Map contains the measure name and the corresponding calculated value
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