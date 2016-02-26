package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.setting.LearningSetting;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public interface IMLAlgorithm {

	public String getName();

	public MLResult learn(Mapping trainingData);
	
	public Mapping computePredictions();

	void init(LearningSetting parameters, Mapping trainingData) throws Exception;
	
	void terminate();
	
}
