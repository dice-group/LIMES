package org.aksw.limes.core.evaluation.quality;

import java.util.Set;

import org.aksw.limes.core.model.Link;

/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public class Recall extends PRF implements QualitativeMeasure {

	@Override
	public double calculate(Set<Link> predictions, Set<Link> goldStandard) {
		if(predictions.size()==0)
			return 0;
		return trueFalsePositive(predictions, goldStandard, true)/(double)goldStandard.size();

	}

}
