package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.evaluation.quantity.PseudoFMeasure;
import org.aksw.limes.core.ml.algorithm.IMLAlgorithm;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public class UnsupervisedLearningSetting extends LearningSetting {
	
	public UnsupervisedLearningSetting(IMLAlgorithm algorithm) {
		super(algorithm);
	}

	@Override
	public void learn() {
		// TODO Auto-generated method stub
		// will use the following
		algorithm.learn(null);
	}
	
	PseudoFMeasure measure = new PseudoFMeasure();

	public PseudoFMeasure getPseudoMeasure() {
		return measure;
	}

	public void setMeasure(PseudoFMeasure measure) {
		this.measure = measure;
	}

}
