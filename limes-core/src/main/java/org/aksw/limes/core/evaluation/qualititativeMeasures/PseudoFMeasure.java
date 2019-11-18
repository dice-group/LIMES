package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo F-Measure.<br>
 * Thereby, not relying on any gold standard. The basic idea is to measure the quality of the
 * given Mapping by calculating how close it is to an assumed 1-to-1 Mapping between source and
 * target.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class PseudoFMeasure extends APseudoPRF {
    static Logger logger = LoggerFactory.getLogger(PseudoFMeasure.class);

    public PseudoFMeasure() {
    }

    /**
     * Use this constructor to toggle between symmetric precision (true) and the older asymmetric
     * Pseudo-Precision (false)
     *
     * @param symmetricPrecision sets/resets the symmetric precision flag
     */
    public PseudoFMeasure(final boolean symmetricPrecision) {
        this();
        this.setSymmetricPrecision(symmetricPrecision);
    }

    /** 
     * The method calculates the pseudo F-Measure of the machine learning predictions compared to a gold standard for beta = 1 .
     * @param predictions The predictions provided by a machine learning algorithm.
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs.
     * @return double - This returns the calculated pseudo F-Measure.
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        return calculate(predictions, goldStandard, 1d);
    }
    /** 
     * The method calculates the pseudo F-Measure of the machine learning predictions compared to a gold standard for different beta values
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @param beta   Beta for F-beta
     * @return double - This returns the calculated pseudo F-Measure
     */
    public double calculate(AMapping predictions, GoldStandard goldStandard, double beta) {
        double p = precision(predictions, goldStandard);
        double r = recall(predictions, goldStandard);
        if (p == 0 && r == 0) return 0.0;
        double f = (1 + beta * beta) * p * r / (beta * beta * p + r);
        return f;
    }

    /** 
     * The method calculates the pseudo recall of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated pseudo recall
     */
    public double recall(AMapping predictions, GoldStandard goldStandard) {
        return new PseudoRecall().calculate(predictions, goldStandard);
    }

    /** 
     * The method calculates the pseudo precision of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated pseudo precision
     */
    public double precision(AMapping predictions, GoldStandard goldStandard) {
        return new PseudoPrecision().calculate(predictions, goldStandard);
    }

}
