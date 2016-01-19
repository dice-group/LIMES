package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.evaluation.quality.PseudoFMeasure;
/**
 * 
 * @author Klaus Lyko
 *
 */
public class EagleUnsupervisedParameters extends EagleParameters{
	PseudoFMeasure measure = new PseudoFMeasure();

	public PseudoFMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(PseudoFMeasure measure) {
		this.measure = measure;
	}
}
