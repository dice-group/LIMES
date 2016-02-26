package org.aksw.limes.core.evaluation.quantity;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public class Precision extends PRF implements QuantitativeMeasure {

	@Override
	public double calculate(Mapping predictions, Mapping goldStandard) {
		if (predictions.size() == 0)
			return 0;
		return trueFalsePositive(predictions, goldStandard, true)
				/ (double) predictions.size();
	}

}
