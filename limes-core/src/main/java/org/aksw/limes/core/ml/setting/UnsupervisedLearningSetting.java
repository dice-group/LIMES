package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFM;
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
	
	PseudoFM measure = new PseudoFM();

	public PseudoFM getPseudoMeasure() {
		return measure;
	}

	public void setMeasure(PseudoFM measure) {
		this.measure = measure;
	}

}
