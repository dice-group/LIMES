package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo F-Measure. 
 * Thereby, not relying on any gold standard. The basic idea is to measure the quality of the
 * a given Mapping by calc. how close it is to an assumed 1-to-1 Mapping between source and 
 * target.
 * @author Klaus Lyko <lyko@informatik.uni-leipzig.de>
 * @author ngonga
 * @author mofeed hassan
 * @version 1.0
 */
public class PseudoFMeasure  extends APseudoPRF {

    public PseudoFMeasure() {}

    /**
     * Use this constructor to toggle between symmetric precision (true) and the older asymmetric
     * Pseudo-Precision (false)
     * @param symmetricPrecision
     */
    public PseudoFMeasure(final boolean symmetricPrecision) {
        this();
        this.setSymmetricPrecision(symmetricPrecision);
    }
    /** Computes the balanced Pseudo-F1-measure.
     *
     * @param sourceUris Source URIs 
     * @param targetUris Target URIs
     * @param result Mapping resulting from ML algorihtms
     * @param beta Beta for F-beta
     * @return Pseudo measure
     */
    @Override
    public double calculate(Mapping predictions, GoldStandard goldStandard) {
        return getPseudoFMeasure(predictions,goldStandard,  1);
    }

    /** Computes Pseudo-f-measure for different beta values
     *
     * @param sourceUris Source URIs 
     * @param targetUris Target URIs
     * @param result Mapping resulting from ML algorihtms
     * @param beta Beta for F-beta
     * @return Pseudo measure
     */
    public double getPseudoFMeasure(Mapping predictions,GoldStandard goldStandard, double beta) {
        double p = new PseudoPrecision().calculate(predictions, goldStandard);// getPseudoPrecision(sourceUris, targetUris, result);
        double r = new PseudoRecall().calculate(predictions, goldStandard); //getPseudoRecall(sourceUris, targetUris, result);        
        if(p==0 && r==0) return 0.0;
        double f = (1 + beta * beta) * p * r / (beta * beta * p + r);
        return f;
    }

}
