package org.aksw.limes.core.ml.algorithm.eagle;

import org.aksw.limes.core.evaluation.quality.QualitativeMeasure;

public class EagleUnsupervisedParameters extends EagleParameters{
	QualitativeMeasure measure;

	public QualitativeMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(QualitativeMeasure measure) {
		this.measure = measure;
	}
}
