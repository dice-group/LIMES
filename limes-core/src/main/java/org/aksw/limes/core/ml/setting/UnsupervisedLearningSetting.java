package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.ml.algorithm.IMLAlgorithm;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class UnsupervisedLearningSetting extends LearningSetting {
	
	public UnsupervisedLearningSetting(IMLAlgorithm algorithm) {
		super(algorithm);
	}

	@Override
	public void learn() {
		// TODO Auto-generated method stub
		// will use the following
		algorithm.learn();
	}

}
