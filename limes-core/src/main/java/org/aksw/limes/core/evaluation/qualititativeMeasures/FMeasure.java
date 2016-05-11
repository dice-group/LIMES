package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.evaluation.evaluator.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public class FMeasure extends PRF implements QualitativeMeasure {

	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		
		double p = new Precision().calculate(predictions, goldStandard);
		double r = new Recall().calculate(predictions, goldStandard);
		
		if(p + r > 0d)
			return 2 * p * r / (p + r);
		else
			return 0d;
		
	}

}
