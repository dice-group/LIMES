package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.HashMap;

public class TrainingInstance {
    private String sourceUri;
    private String targetUri;
    private double classLabel;
    private HashMap<String,Double> measureValues;
    
	public TrainingInstance(String sourceUri, String targetUri, double classLabel) {
		this.sourceUri = sourceUri;
		this.targetUri = targetUri;
		this.classLabel = classLabel;
		this.measureValues = new HashMap<String,Double>();
	}
	
	@Override
	public String toString(){
		return this.sourceUri + " -> " + this.targetUri + " : " + this.classLabel;
	}

	public String getSourceUri() {
		return sourceUri;
	}

	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}

	public String getTargetUri() {
		return targetUri;
	}

	public void setTargetUri(String targetUri) {
		this.targetUri = targetUri;
	}

	public double getClassLabel() {
		return classLabel;
	}

	public void setClassLabel(double classLabel) {
		this.classLabel = classLabel;
	}

	public HashMap<String, Double> getMeasureValues() {
		return measureValues;
	}

	public void setMeasureValues(HashMap<String, Double> measureValues) {
		this.measureValues = measureValues;
	}
    
    
}
