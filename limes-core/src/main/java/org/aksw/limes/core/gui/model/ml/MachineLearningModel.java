package org.aksw.limes.core.gui.model.ml;

import java.util.HashMap;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.Lion;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;
import org.aksw.limes.core.ml.setting.ActiveLearningSetting;
import org.aksw.limes.core.ml.setting.BatchLearningSetting;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;

public abstract class MachineLearningModel {

	protected HashMap<String,?> params;
	
//	protected MachineLearningView mlview;
	
	protected MLAlgorithm mlalgorithm;
	
	protected LearningSetting learningsetting;
	
	protected Config config;
	
	protected Cache sourceCache;
	
	protected Cache targetCache;
	
	public MachineLearningModel(Config config, Cache sourceCache, Cache targetCache){
		this.config = config;
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
	}
	
	
	public abstract Mapping learn();


	public MLAlgorithm getMlalgorithm() {
		return mlalgorithm;
	}


	public LearningSetting getLearningsetting() {
		return learningsetting;
	}

	public void initializeData(String algorithmName){
		switch(algorithmName){
		case "Lion":
			this.mlalgorithm = new Lion(sourceCache, targetCache, config);
			break;
		default:
			System.err.println("Unknown algorithm");
		}
		if(this instanceof ActiveLearningModel){
			this.learningsetting = new ActiveLearningSetting(mlalgorithm);
		}else if(this instanceof BatchLearningModel){
			this.learningsetting = new BatchLearningSetting(mlalgorithm);
		}else if(this instanceof UnsupervisedLearningModel){
			this.learningsetting = new UnsupervisedLearningSetting(mlalgorithm);
		}else{
			System.err.println("Unknown subclass of MachineLearningModel");
		}
	}

	public void setMlalgorithm(MLAlgorithm mlalgorithm) {
		this.mlalgorithm = mlalgorithm;
	}


	public void setLearningsetting(LearningSetting learningsetting) {
		this.learningsetting = learningsetting;
	}

}
