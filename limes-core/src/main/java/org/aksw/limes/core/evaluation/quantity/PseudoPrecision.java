package org.aksw.limes.core.evaluation.quantity;

import java.util.List;

import org.aksw.limes.core.io.mapping.Mapping;

public class PseudoPrecision extends PseudoPRF {

public PseudoPrecision() {}
	
	/**
	 * Use this constructor to toggle between symmetric precision (true) and the older asymmetric
	 * Pseudo-Precision (false)
	 * @param symmetricPrecision
	 */
	public PseudoPrecision(final boolean symmetricPrecision) {
		this();
		this.symmetricPrecision = symmetricPrecision;
	}
	
    /** Computes the pseudo-precision, which is basically how well the mapping 
     * maps one single s to one single t
     * @param sourceUris List of source uris
     * @param targetUris List of target uris
     * @param result Mapping of source to targer uris
     * @return Pseudo precision score
     */
	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		Mapping res = predictions;
    	Mapping rev = res.reverseSourceTarget();
    	if(use1To1Mapping) {
    		res = predictions.getBestOneToNMapping();
    		rev = res.reverseSourceTarget().getBestOneToNMapping();
    	}
    	double p = res.getMap().keySet().size();
    	if(symmetricPrecision)
    		p = res.getMap().keySet().size()+rev.getMap().keySet().size();
        double q = 0;
        for (String s : predictions.getMap().keySet()) {
        	if(symmetricPrecision)
        		q = q + 2*predictions.getMap().get(s).size();
        	else
        		q = q + predictions.getMap().get(s).size();
        }
        if(p==0 || q==0) return 0;
        return p / q;
	}
	
	

}
