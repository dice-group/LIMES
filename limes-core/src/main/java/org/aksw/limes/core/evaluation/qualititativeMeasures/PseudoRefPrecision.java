package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.log4j.Logger;

public class PseudoRefPrecision extends PseudoPrecision {
    static Logger logger = Logger.getLogger(PseudoRefPrecision.class);

    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        AMapping res = predictions;
        if (useOneToOneMapping)
            res = predictions.getBestOneToOneMappings(predictions); // the first call of prediction just to call the method; ya i know
        double p = res.getMap().keySet().size();
        double q = 0;
        for (String s : res.getMap().keySet()) {
            q = q + res.getMap().get(s).size();
        }
        if (p == 0 || q == 0) return 0;
        return p / q;
    }
}
