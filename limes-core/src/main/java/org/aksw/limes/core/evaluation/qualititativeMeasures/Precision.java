package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * It can be defined as the ratio of the retrieved correct results relative to the total number of the retrieved results,i.e. Tp/(Tp+Fp).
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 1.0
 *
 */
public class Precision extends APRF implements IQualitativeMeasure {

    @Override
    public double calculate(Mapping predictions, GoldStandard goldStandard) {
        if (predictions.size() == 0)
            return 0;
        return trueFalsePositive(predictions, goldStandard.goldStandard, true)	/ (double) predictions.size();
    }

}
