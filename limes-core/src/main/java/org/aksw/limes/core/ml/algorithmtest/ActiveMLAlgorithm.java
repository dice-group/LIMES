package org.aksw.limes.core.ml.algorithmtest;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;

public class ActiveMLAlgorithm extends AActiveMLAlgorithm {
	
	public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;
	
	public ActiveMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) {
		
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
	public Mapping getNextExamples(int size) {
		return ml.getNextExamples(size);
	}

	@Override
	public MLModel activeLearn(Mapping oracleMapping) {
		return ml.activeLearn(oracleMapping);
	}

	
}
