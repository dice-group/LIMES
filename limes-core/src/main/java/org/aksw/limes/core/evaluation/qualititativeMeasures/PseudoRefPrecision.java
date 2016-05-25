package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

public class PseudoRefPrecision extends PseudoPrecision{
	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		Mapping res = predictions;
    	if(useOneToOneMapping)
    		res = predictions.getBestOneToOneMappings(predictions); // the first call of prediction just to call the method; ya i know
        double p = res.getMap().keySet().size();
        double q = 0;
        for (String s : res.getMap().keySet()) {
            q = q + res.getMap().get(s).size();
        }
        if(p==0 || q==0) return 0;
        return p / q;
	}
}
