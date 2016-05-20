package org.aksw.limes.core.ml.algorithmtest;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLModel;


public abstract class ASupervisedMLAlgorithm extends AMLAlgorithm {

	public abstract MLModel learn(Mapping trainingData);

}
