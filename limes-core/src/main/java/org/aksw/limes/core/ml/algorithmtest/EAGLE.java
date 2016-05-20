package org.aksw.limes.core.ml.algorithmtest;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;

public class EAGLE extends ACoreMLAlgorithm {

	@Override
	protected String getName() {
		return "EAGLE";
	}

	@Override
	protected void init(LearningSetting ls, Cache source, Cache target) {
		// TODO Auto-generated method stub
	}

	@Override
	protected MLModel learn(Mapping trainingData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MLModel learn(PseudoFMeasure pfm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Mapping predict(Cache source, Cache target, MLModel mlModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
	}

	@Override
	protected Mapping getNextExamples(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected MLModel activeLearn(Mapping oracleMapping) {
		throw new UnsupportedOperationException();
	}

}
