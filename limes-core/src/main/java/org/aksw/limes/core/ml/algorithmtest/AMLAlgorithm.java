package org.aksw.limes.core.ml.algorithmtest;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;

public abstract class AMLAlgorithm {
	
	protected abstract void init(LearningSetting ls, Cache source, Cache target);

	protected abstract Mapping predict(Cache source, Cache target, MLModel mlModel);

}
