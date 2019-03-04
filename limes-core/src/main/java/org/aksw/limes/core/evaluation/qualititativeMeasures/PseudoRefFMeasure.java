package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo Reference F-Measure.<br>
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
public class PseudoRefFMeasure extends PseudoFMeasure {
    static Logger logger = LoggerFactory.getLogger(PseudoRefFMeasure.class);

    /** 
     * The method calculates the pseudo reference recall of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated reference pseudo recall
     */
    public double recall(AMapping predictions, GoldStandard goldStandard) {
        return new PseudoRefRecall().calculate(predictions, goldStandard);
    }

    /** 
     * The method calculates the pseudo reference precision of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated pseudo reference precision
     */
    public double precision(AMapping predictions, GoldStandard goldStandard) {
        return new PseudoRefPrecision().calculate(predictions, goldStandard);
    }
}
