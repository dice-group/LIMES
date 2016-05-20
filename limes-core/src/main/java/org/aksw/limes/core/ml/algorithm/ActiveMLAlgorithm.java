package org.aksw.limes.core.ml.algorithm;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;

public class ActiveMLAlgorithm extends AActiveMLAlgorithm {
	
	public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;
	
	public ActiveMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {
		
		try {
			Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
			ml = ctor.newInstance();
		} catch (Exception e) {
			throw new UnsupportedMLImplementationException(ml.getName());
		}
		
		if(!ml.supports(ML_IMPLEMENTATION_TYPE)) {
			throw new UnsupportedMLImplementationException(ml.getName());
		}
		
	}
	
	@Override
	public Mapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		return ml.getNextExamples(size);
	}

	@Override
	public MLModel activeLearn(Mapping oracleMapping) throws UnsupportedMLImplementationException {
		return ml.activeLearn(oracleMapping);
	}

	
}
