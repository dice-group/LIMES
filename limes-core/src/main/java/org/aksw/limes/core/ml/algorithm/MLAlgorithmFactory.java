package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;

public class MLAlgorithmFactory {

	public static AMLAlgorithm createMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz, MLImplementationType mlType) throws UnsupportedMLImplementationException {
		
		switch(mlType) {
		case SUPERVISED_BATCH:
			return new SupervisedMLAlgorithm(clazz);
		case UNSUPERVISED:
			return new UnsupervisedMLAlgorithm(clazz);
		case SUPERVISED_ACTIVE:
			return new ActiveMLAlgorithm(clazz);
		default:
			throw new UnsupportedMLImplementationException(clazz.getName());
		}
		
	}
	
}
