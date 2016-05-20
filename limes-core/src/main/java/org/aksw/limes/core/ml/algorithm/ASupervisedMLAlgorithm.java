package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;


public abstract class ASupervisedMLAlgorithm extends AMLAlgorithm {

	public abstract MLModel learn(Mapping trainingData) throws UnsupportedMLImplementationException;

}
