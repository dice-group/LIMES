package org.aksw.limes.core.evaluation.quality;

import java.util.Set;

import org.aksw.limes.core.model.Link;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public class FMeasure implements QualitativeMeasure {

	@Override
	public double calculate(Set<Link> predictions, Set<Link> goldStandard) {
		
		double p = new Precision().calculate(predictions, goldStandard);
		double r = new Recall().calculate(predictions, goldStandard);
		
		if(p + r > 0d)
			return 2 * p * r / (p + r);
		else
			return 0d;
		
	}

}
