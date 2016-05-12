/**
 * 
 */
package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author mofeed
 *
 */
public class PseudoRefRecall extends PseudoRecall{
	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		Mapping res = predictions;
    	if(use1To1Mapping)
    		res = predictions.getBestOneToOneMappings(predictions);// the first call of prediction just to call the method; ya i know
        double size = 0;
        for (String s : res.getMap().keySet()) {
            
            size = size + res.getMap().get(s).size();
        }
        return size/ Math.min(goldStandard.sourceUris.size(), goldStandard.targetUris.size());
	}
}
