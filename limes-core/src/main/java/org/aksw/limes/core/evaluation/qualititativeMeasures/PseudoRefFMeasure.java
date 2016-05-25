package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

public class PseudoRefFMeasure extends PseudoFMeasure{
    public double calculate(Mapping predictions, GoldStandard goldStandard, double beta) {
        double p = precision(predictions, goldStandard);
        double r = recall(predictions, goldStandard);        
        if(p==0 && r==0) return 0.0;
        double f = (1 + beta * beta) * p * r / (beta * beta * p + r);
        return f;
    }

    public double recall(Mapping predictions, GoldStandard goldStandard) {
        return new PseudoRefRecall().calculate(predictions, goldStandard);
    }

    public double precision(Mapping predictions, GoldStandard goldStandard) {
        return new PseudoRefPrecision().calculate(predictions, goldStandard);
    }
}
