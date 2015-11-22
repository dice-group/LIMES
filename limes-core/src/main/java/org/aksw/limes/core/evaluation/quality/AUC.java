package org.aksw.limes.core.evaluation.quality;

import java.util.Set;

import org.aksw.limes.core.model.Link;

/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 */
public class AUC extends PRF implements QualitativeMeasure {

	@Override
	public double calculate(Set<Link> predictions, Set<Link> goldStandard) {
		

		return 0;
	}

}
