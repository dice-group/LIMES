package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo F-Measure.
 * Thereby, not relying on any gold standard. The basic idea is to measure the quality of the
 * a given Mapping by calc. how close it is to an assumed 1-to-1 Mapping between source and
 * target.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class PseudoPrecision extends APseudoPRF {
    static Logger logger = LoggerFactory.getLogger(PseudoPrecision.class);

    public PseudoPrecision() {
    }

    /**
     * Use this constructor to toggle between symmetric precision (true) and the older asymmetric Pseudo-Precision (false)
     *
     * @param symmetricPrecision sets/resets the symmetric precision flag
     */
    public PseudoPrecision(final boolean symmetricPrecision) {
        this();
        this.setSymmetricPrecision(symmetricPrecision);
    }

    /** 
     * The method calculates the pseudo precision of the machine learning predictions compared to a gold standard , which is basically how well the mapping
     * maps one single s to one single t.
     * @param predictions The predictions provided by a machine learning algorithm.
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs.
     * @return double - This returns the calculated pseudo precision.
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
		predictions = predictions.getOnlyPositiveExamples();
		AMapping res = predictions;
        AMapping rev = res.reverseSourceTarget();
        if (useOneToOneMapping) {
            res = predictions.getBestOneToOneMappings(predictions);
            rev = res.reverseSourceTarget();
        }
        double p = res.getMap().keySet().size();
        if (isSymmetricPrecision())
            p = res.getMap().keySet().size() + rev.getMap().keySet().size();
        double q = 0;
        for (String s : predictions.getMap().keySet()) {
            if (isSymmetricPrecision())
                q = q + 2 * predictions.getMap().get(s).size();
            else
                q = q + predictions.getMap().get(s).size();
        }
        if (p == 0 || q == 0) return 0;
        return p / q;
    }

}
