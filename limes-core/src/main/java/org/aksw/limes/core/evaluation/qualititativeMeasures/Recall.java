package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public class Recall extends PRF implements QuantitativeMeasure {

	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		if(predictions.size()==0)
			return 0;
		return trueFalsePositive(predictions, goldStandard.goldStandard, true)/(double)goldStandard.goldStandard.size();

	}

}
