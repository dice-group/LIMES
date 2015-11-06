package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.ml.algorithm.MLAlgorithm;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public interface LearningSetting {

	public void learn(MLAlgorithm algorithm);
	
}
