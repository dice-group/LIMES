package org.aksw.limes.core.evaluation.evaluator;

import org.aksw.limes.core.evaluation.qualititativeMeasures.*;

/**
 * This Factory class give a measure object based on the specified measure type.
 * This object can be used through its method calculate() to evaluate the retrieved mappings
 *
 * @author mofeed
 * @version 1.0
 */
public class EvaluatorFactory {

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
