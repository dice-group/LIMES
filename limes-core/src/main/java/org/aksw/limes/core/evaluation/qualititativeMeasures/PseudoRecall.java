package org.aksw.limes.core.evaluation.qualititativeMeasures;

import java.util.HashSet;
import java.util.Set;

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
 *
 */
public class PseudoRecall  extends APseudoPRF {
	public PseudoRecall() {}
	
	/**
	 * Use this constructor to toggle between symmetric precision (true) and the older asymmetric
	 * Pseudo-Precision (false)
	 * @param symmetricPrecision
	 */
	public PseudoRecall(final boolean symmetricPrecision) {
		this();
		this.setSymmetricPrecision(symmetricPrecision);
	}
	
	
	/** The assumption here is a follows. We compute how many of the s and t
     * were mapped. 
     * @param sourceUris URIs in source cache
     * @param targetUris URIs in target cache
     * @param result Mapping computed by our learner
     * @param Run mapping minimally and apply filtering. Compare the runtime of both approaches
     * @return Pseudo recall 
     */
	
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
    	Mapping res = predictions;
    	if(use1To1Mapping) {
    		res = predictions.getBestOneToNMapping();
    	}
        double q = res.getMap().keySet().size();
        Set<String> values = new HashSet<String>();
        for (String s : res.getMap().keySet()) {
            for(String t: res.getMap().get(s).keySet())
            {
                values.add(t);
            }
        }
        double reference = (double)(goldStandard.sourceUris.size() + goldStandard.targetUris.size());
        return (q + values.size())/ reference;
	}
}
