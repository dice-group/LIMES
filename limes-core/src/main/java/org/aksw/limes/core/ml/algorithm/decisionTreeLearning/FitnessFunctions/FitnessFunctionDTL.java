package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions;

import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTree;

public abstract class FitnessFunctionDTL {
	protected DecisionTree dt;
	public abstract ExtendedClassifier getBestClassifier(DecisionTree currentNode);
	public abstract boolean stopCondition(DecisionTree currentNode);
	
	public void setDt(DecisionTree dt){
		this.dt = dt;
	}
}
