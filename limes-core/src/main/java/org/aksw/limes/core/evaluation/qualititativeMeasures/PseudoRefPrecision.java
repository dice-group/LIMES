package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo Reference Precision.<br>
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
public class PseudoRefPrecision extends PseudoPrecision {
    static Logger logger = LoggerFactory.getLogger(PseudoRefPrecision.class);

    /** 
     * The method calculates the pseudo reference Precision of the machine learning predictions compared to a gold standard for beta = 1 .
     * @param predictions The predictions provided by a machine learning algorithm.
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs.
     * @return double - This returns the calculated pseudo reference Precision.
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        AMapping res = predictions.getOnlyPositiveExamples();
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
