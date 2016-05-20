package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;

public class EAGLE extends ACoreMLAlgorithm {
	
	protected EAGLE() {
		//
	}

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
	protected Mapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException(this.getName());
	}

	@Override
	protected MLModel activeLearn(Mapping oracleMapping) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException(this.getName());
	}

}
