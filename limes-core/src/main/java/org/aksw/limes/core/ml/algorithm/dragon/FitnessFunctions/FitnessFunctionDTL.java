package org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions;

import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

public abstract class FitnessFunctionDTL {
	protected DecisionTree dt;
	public abstract ExtendedClassifier getBestClassifier(DecisionTree currentNode);
	public abstract boolean stopCondition(DecisionTree currentNode);
	protected PropertyMapping propertyMapping;
	
	public void setDt(DecisionTree dt){
		this.dt = dt;
	}

	public PropertyMapping getPropertyMapping() {
		return propertyMapping;
	}
	public void setPropertyMapping(PropertyMapping propertyMapping) {
		this.propertyMapping = propertyMapping;
	}

}
