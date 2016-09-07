package org.aksw.limes.core.evaluation.qualititativeMeasures;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo F-Measure.
 * Thereby, not relying on any gold standard. The basic idea is to measure the quality of the
 * a given Mapping by calc. how close it is to an assumed 1-to-1 Mapping between source and
 * target.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @deprecated Use {@link PseudoFMeasure} instead
 */
@Deprecated
public class PseudoFM {
    /* FIXME QualitiveMeasure interface is not suitable, as we need additional
     * input: URIs of source and target. 
     */

    public boolean symmetricPrecision = true;
    boolean use1To1Mapping = false;

    public PseudoFM() {
    }

    /**
     * Use this constructor to toggle between symmetric precision (true) and the older asymmetric
     * Pseudo-Precision (false)
     *
     * @param symmetricPrecision sets/clears the flag for symmetric precision
     */
    public PseudoFM(final boolean symmetricPrecision) {
        this();
        this.symmetricPrecision = symmetricPrecision;
    }

    /**
     * @return the use1To1Mapping
     */
    public boolean isUse1To1Mapping() {
        return use1To1Mapping;
    }

    public void setUse1To1Mapping(boolean use1To1Mapping) {
        this.use1To1Mapping = use1To1Mapping;
    }

    /**
     * Computes the balanced Pseudo-F1-measure.
     *
     * @param sourceUris
     *         Source URIs
     * @param targetUris
     *         Target URIs
     * @param result
     *         Mapping resulting from ML algorihtms
     * @return Pseudo measure
     */
    public double getPseudoFMeasure(List<String> sourceUris, List<String> targetUris,
                                    AMapping result) {
        return getPseudoFMeasure(sourceUris, targetUris, result, 1);
    }

    /**
     * Computes Pseudo-f-measure for different beta values
     *
     * @param sourceUris
     *         Source URIs
     * @param targetUris
     *         Target URIs
     * @param result
     *         Mapping resulting from ML algorihtms
     * @param beta
     *         Beta for F-beta
     * @return Pseudo measure
     */
    public double getPseudoFMeasure(List<String> sourceUris, List<String> targetUris,
                                    AMapping result, double beta) {
        double p = getPseudoPrecision(sourceUris, targetUris, result);
        double r = getPseudoRecall(sourceUris, targetUris, result);
        if (p == 0 && r == 0) return 0.0;
        double f = (1 + beta * beta) * p * r / (beta * beta * p + r);
        return f;
    }

    /**
     * Computes the pseudo-precision, which is basically how well the mapping
     * maps one single s to one single t
     *
     * @param sourceUris
     *         List of source uris
     * @param targetUris
     *         List of target uris
     * @param result
     *         Mapping of source to targer uris
     * @return double-Pseudo precision score
     */
    public double getPseudoPrecision(List<String> sourceUris, List<String> targetUris, AMapping result) {
        AMapping res = result;
        AMapping rev = res.reverseSourceTarget();
        if (use1To1Mapping) {
            res = result.getBestOneToNMapping();
            rev = res.reverseSourceTarget().getBestOneToNMapping();
        }
        double p = res.getMap().keySet().size();
        if (symmetricPrecision)
            p = res.getMap().keySet().size() + rev.getMap().keySet().size();
        double q = 0;
        for (String s : result.getMap().keySet()) {
            if (symmetricPrecision)
                q = q + 2 * result.getMap().get(s).size();
            else
                q = q + result.getMap().get(s).size();
        }
        if (p == 0 || q == 0) return 0;
        return p / q;
    }

    /**
     * The assumption here is a follows. We compute how many of the s and t
     * were mapped. 
     * @param sourceUris URIs in source cache
     * @param targetUris URIs in target cache
     * @param result Mapping computed by our learner
     * @return double-Pseudo recall
     */
    public double getPseudoRecall(List<String> sourceUris, List<String> targetUris,
                                  AMapping result) {
        AMapping res = result;
        if (use1To1Mapping) {
            res = result.getBestOneToNMapping();
        }
        double q = res.getMap().keySet().size();
        Set<String> values = new HashSet<String>();
        for (String s : res.getMap().keySet()) {
            for (String t : res.getMap().get(s).keySet()) {
                values.add(t);
            }
        }
        double reference = (double) (sourceUris.size() + targetUris.size());
        return (q + values.size()) / reference;
    }


}
