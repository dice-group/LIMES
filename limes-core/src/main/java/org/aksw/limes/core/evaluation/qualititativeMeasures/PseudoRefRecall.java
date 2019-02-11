/**
 *
 */
package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo Reference Recall.<br>
 * Thereby, not relying on any gold standard. The basic idea is to measure the quality of the
 * given Mapping by calculating how close it is to an assumed 1-to-1 Mapping between source and
 * target.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class PseudoRefRecall extends PseudoRecall {
    static Logger logger = LoggerFactory.getLogger(PseudoRefRecall.class);

    /** 
     * The method calculates the pseudo reference Recall of the machine learning predictions compared to a gold standard for beta = 1 .
     * @param predictions The predictions provided by a machine learning algorithm.
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs.
     * @return double - This returns the calculated pseudo reference Recall.
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        AMapping res = predictions;
        if (useOneToOneMapping)
            res = predictions.getBestOneToOneMappings(predictions);// the first call of prediction just to call the method; ya i know
        res.getSize();
        double size = 0;
        for (String s : res.getMap().keySet()) {

            size = size + res.getMap().get(s).size();
        }
        return size / Math.min(goldStandard.sourceUris.size(), goldStandard.targetUris.size());
    }
}
