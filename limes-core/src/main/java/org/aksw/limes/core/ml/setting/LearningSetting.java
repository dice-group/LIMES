package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.ml.algorithm.IMLAlgorithm;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public abstract class LearningSetting {
	
	protected IMLAlgorithm algorithm;

	public LearningSetting(IMLAlgorithm algorithm) {
		super();
		this.algorithm = algorithm;
	}

	public abstract void learn();

	public IMLAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	
	
}
