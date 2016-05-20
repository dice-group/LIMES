package org.aksw.limes.core.ml.algorithmtest;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;

public class SupervisedMLAlgorithm extends ASupervisedMLAlgorithm {
	
	public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_BATCH;
	
	public SupervisedMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) {
		
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
	public MLModel learn(Mapping trainingData) {
		return ml.learn(trainingData);
	}

	
}
