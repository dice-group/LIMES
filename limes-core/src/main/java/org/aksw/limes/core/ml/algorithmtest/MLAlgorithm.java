package org.aksw.limes.core.ml.algorithmtest;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;

public class MLAlgorithm extends AMLAlgorithm {
	
	private ACoreMLAlgorithm ml;
	private MLImplementationType mlType;
	
	public MLAlgorithm(ACoreMLAlgorithm ml, MLImplementationType mlType) {
		
		if(ml.supports(mlType)) {
			this.ml = ml;
			this.mlType = mlType;
		} else {
			throw new UnsupportedOperationException(mlType.name() + " implementation of "+ml.getName()+" not supported.");
		}
		
	}
	
	@Override
	public void init(LearningSetting ls, Cache source, Cache target) {
		ml.init(ls, source, target);
	}

	@Override
	public Mapping predict(Cache source, Cache target, MLModel mlModel) {
		return ml.predict(source, target, mlModel);
	}
	
	/**
	 * XXX not sure about this!
	 * 
	 * @param trainingData
	 * @return
	 */
	public MLModel learn(Mapping trainingData) {
		if(mlType == MLImplementationType.SUPERVISED_BATCH)
			return ml.learn(trainingData);
		return ml.activeLearn(trainingData);
	}

	/**
	 * XXX not sure about this!
	 * 
	 * @param pfm
	 * @return
	 */
	public MLModel learn(PseudoFMeasure pfm) {
		return ml.learn(pfm);
	}


}
