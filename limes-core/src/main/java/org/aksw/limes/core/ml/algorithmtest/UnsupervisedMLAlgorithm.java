package org.aksw.limes.core.ml.algorithmtest;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.ml.algorithm.MLModel;

public class UnsupervisedMLAlgorithm extends AUnsupervisedMLAlgorithm {
	
	public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.UNSUPERVISED;
	
	public UnsupervisedMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) {
		
		try {
			Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
			ml = ctor.newInstance();
		} catch (Exception e) {
			throw new UnsupportedOperationException(ML_IMPLEMENTATION_TYPE + " implementation of "+ml.getName()+" not supported.");
		}
		
		if(!ml.supports(ML_IMPLEMENTATION_TYPE)) {
			throw new UnsupportedOperationException(ML_IMPLEMENTATION_TYPE + " implementation of "+ml.getName()+" not supported.");
		}
		
	}
	
	@Override
	public MLModel learn(PseudoFMeasure pfm) {
		return ml.learn(pfm);
	}

	
}
