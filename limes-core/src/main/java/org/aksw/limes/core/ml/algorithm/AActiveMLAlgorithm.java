package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;


public abstract class AActiveMLAlgorithm extends AMLAlgorithm {
	
	public abstract Mapping getNextExamples(int size) throws UnsupportedMLImplementationException;

	public abstract MLModel activeLearn(Mapping oracleMapping) throws UnsupportedMLImplementationException;

}
