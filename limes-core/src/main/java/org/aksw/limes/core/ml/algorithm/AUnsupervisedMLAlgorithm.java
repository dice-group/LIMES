package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;


public abstract class AUnsupervisedMLAlgorithm extends AMLAlgorithm {

	public abstract MLModel learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException;

}
