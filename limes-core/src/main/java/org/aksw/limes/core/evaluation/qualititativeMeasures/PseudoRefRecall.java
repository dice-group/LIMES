/**
 *
 */
package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.log4j.Logger;

/**
 * @author mofeed
 */
public class PseudoRefRecall extends PseudoRecall {
    static Logger logger = Logger.getLogger(PseudoRefRecall.class);

    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        AMapping res = predictions;
        if (useOneToOneMapping)
            res = predictions.getBestOneToOneMappings(predictions);// the first call of prediction just to call the method; ya i know
        double size = 0;
        for (String s : res.getMap().keySet()) {

            size = size + res.getMap().get(s).size();
        }
        return size / Math.min(goldStandard.sourceUris.size(), goldStandard.targetUris.size());
    }
}
