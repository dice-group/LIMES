package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * F-Measure is the weighted average of the precision and recall
 *
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class FMeasure extends APRF implements IQualitativeMeasure {
    static Logger logger = LoggerFactory.getLogger(FMeasure.class);

    /**
     * The method calculates the F-Measure of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated F-Measure
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        return calculate(predictions, goldStandard, 1d);
    }

    /** 
     * The method calculates the F-Measure of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated F-Measure
     */
    public double calculate(AMapping predictions, GoldStandard goldStandard, double beta) {

        double p = precision(predictions, goldStandard);
        double r = recall(predictions, goldStandard);
        double beta2 = Math.pow(beta, 2);

        if (p + r > 0d)
            return (1 + beta2) * p * r / ((beta2 * p) + r);
        else
            return 0d;

    }

    /** 
     * The method calculates the recall of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated recall
     */
    public double recall(AMapping predictions, GoldStandard goldStandard) {
        return new Recall().calculate(predictions, goldStandard);
    }

    /** 
     * The method calculates the precision of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated precision
     */
    public double precision(AMapping predictions, GoldStandard goldStandard) {
        return new Precision().calculate(predictions, goldStandard);
    }

}
