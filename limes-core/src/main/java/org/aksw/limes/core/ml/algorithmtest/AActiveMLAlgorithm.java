package org.aksw.limes.core.ml.algorithmtest;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;


public abstract class AActiveMLAlgorithm extends AMLAlgorithm {
	
	protected abstract Mapping getNextExamples(int size);

	protected abstract MLModel activeLearn(Mapping oracleMapping);

}
