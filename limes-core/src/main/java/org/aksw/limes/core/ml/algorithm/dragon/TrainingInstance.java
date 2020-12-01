package org.aksw.limes.core.ml.algorithm.dragon;

import java.util.HashMap;
import java.util.Map;

public class TrainingInstance {
	private String sourceUri;
	private String targetUri;
	private double classLabel;
	private Map<String, Double> measureValues;

	public TrainingInstance(String sourceUri, String targetUri, double classLabel) {
		this.sourceUri = sourceUri;
		this.targetUri = targetUri;
		this.classLabel = classLabel;
		this.measureValues = new HashMap<>();
	}

	@Override
	public String toString() {
		return this.sourceUri + " -> " + this.targetUri + " : " + this.classLabel;
	}

	public String getSourceUri() {
		return this.sourceUri;
	}

	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}

	public String getTargetUri() {
		return this.targetUri;
	}

	public void setTargetUri(String targetUri) {
		this.targetUri = targetUri;
	}

	public double getClassLabel() {
		return this.classLabel;
	}

	public void setClassLabel(double classLabel) {
		this.classLabel = classLabel;
	}

	public Map<String, Double> getMeasureValues() {
		return this.measureValues;
	}

	public void setMeasureValues(Map<String, Double> measureValues) {
		this.measureValues = measureValues;
	}

}
