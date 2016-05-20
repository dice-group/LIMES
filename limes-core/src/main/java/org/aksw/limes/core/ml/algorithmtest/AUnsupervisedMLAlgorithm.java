package org.aksw.limes.core.ml.algorithmtest;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.ml.algorithm.MLModel;


public abstract class AUnsupervisedMLAlgorithm extends AMLAlgorithm {

	protected abstract MLModel learn(PseudoFMeasure pfm);

}
