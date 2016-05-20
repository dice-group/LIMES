package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;

public abstract class AMLAlgorithm {
		
	protected ACoreMLAlgorithm ml;
	
	public String getName() {
		return ml.getName();
	}

	public void init(LearningParameters lp, Cache source, Cache target) {
		ml.init(lp, source, target);
	}

	public Mapping predict(Cache source, Cache target, MLModel mlModel) {
		return ml.predict(source, target, mlModel);
	}
	
	public SupervisedMLAlgorithm asSupervised() {
		return (SupervisedMLAlgorithm) this;
	}

	public UnsupervisedMLAlgorithm asUnsupervised() {
		return (UnsupervisedMLAlgorithm) this;
	}

	public ActiveMLAlgorithm asActive() {
		return (ActiveMLAlgorithm) this;
	}
	
	public LearningParameters getParameters() {
		return ml.getParameters();
	}

}
