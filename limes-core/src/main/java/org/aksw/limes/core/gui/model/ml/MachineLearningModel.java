package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.EagleUnsupervised;
import org.aksw.limes.core.ml.algorithm.Lion;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;
import org.aksw.limes.core.ml.setting.ActiveLearningSetting;
import org.aksw.limes.core.ml.setting.BatchLearningSetting;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.apache.log4j.Logger;

public abstract class MachineLearningModel {

//	protected MachineLearningView mlview;
	
	protected MLAlgorithm mlalgorithm;
	
	protected static Logger logger = Logger.getLogger("LIMES");
	
	protected LearningSetting learningsetting;
	
	protected Config config;
	
	protected Cache sourceCache;
	
	protected Cache targetCache;
	
	protected Thread learningThread;
	
	protected Mapping learnedMapping;
	
	public MachineLearningModel(Config config, Cache sourceCache, Cache targetCache){
		this.config = config;
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
	}
	
	
	public abstract void learn(MachineLearningView view);


	public MLAlgorithm getMlalgorithm() {
		return mlalgorithm;
	}


	public LearningSetting getLearningsetting() {
		return learningsetting;
	}

	public void initializeData(String algorithmName){
		//TODO other cases
		switch(algorithmName){
		case "Lion":
			this.mlalgorithm = new Lion(sourceCache, targetCache, config);
			break;
		case "Eagle":
			if (this instanceof UnsupervisedLearningModel){
			this.mlalgorithm = new EagleUnsupervised(sourceCache, targetCache, config);
			}else{
				logger.info("Not implemented yet");
			}
			break;
		default:
			logger.info("Unknown algorithm");
		}
		if(this instanceof ActiveLearningModel){
			this.learningsetting = new ActiveLearningSetting(mlalgorithm);
		}else if(this instanceof BatchLearningModel){
			this.learningsetting = new BatchLearningSetting(mlalgorithm);
		}else if(this instanceof UnsupervisedLearningModel){
			this.learningsetting = new UnsupervisedLearningSetting(mlalgorithm);
		}else{
			logger.info("Unknown subclass of MachineLearningModel");
		}
	}

	public void setMlalgorithm(MLAlgorithm mlalgorithm) {
		this.mlalgorithm = mlalgorithm;
	}


	public void setLearningsetting(LearningSetting learningsetting) {
		this.learningsetting = learningsetting;
	}

}
