package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PseudoRefFMeasure extends PseudoFMeasure {
    static Logger logger = LoggerFactory.getLogger(PseudoRefFMeasure.class);

    public double calculate(AMapping predictions, GoldStandard goldStandard, double beta) {

        double p = precision(predictions, goldStandard);
        double r = recall(predictions, goldStandard);
        if (p == 0 && r == 0) return 0.0;
        double f = (1 + beta * beta) * p * r / (beta * beta * p + r);
        return f;
    }

    public double recall(AMapping predictions, GoldStandard goldStandard) {
        return new PseudoRefRecall().calculate(predictions, goldStandard);
    }

    public double precision(AMapping predictions, GoldStandard goldStandard) {
        return new PseudoRefPrecision().calculate(predictions, goldStandard);
    }
}
