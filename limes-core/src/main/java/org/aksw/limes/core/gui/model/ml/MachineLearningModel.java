package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.EagleUnsupervised;
import org.aksw.limes.core.ml.oldalgorithm.Lion;
import org.aksw.limes.core.ml.oldalgorithm.MLAlgorithm;
import org.aksw.limes.core.ml.setting.ActiveLearningSetting;
import org.aksw.limes.core.ml.setting.BatchLearningSetting;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.apache.log4j.Logger;

import javafx.concurrent.Task;

public abstract class MachineLearningModel {

//	protected MachineLearningView mlview;
	
	protected MLAlgorithm mlalgorithm;
	
	protected static Logger logger = Logger.getLogger("LIMES");
	
	protected LearningSetting learningsetting;
	
	private Config config;
	
	protected Cache sourceCache;
	
	protected Cache targetCache;
	
	protected Thread learningThread;
	
	private Mapping learnedMapping;
	
	public MachineLearningModel(Config config, Cache sourceCache, Cache targetCache){
		this.setConfig(config);
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
	}
	
	
	public abstract Task<Void> createLearningTask();


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
			this.mlalgorithm = new Lion(sourceCache, targetCache, getConfig());
			break;
		case "Eagle":
			if (this instanceof UnsupervisedLearningModel){
			this.mlalgorithm = new EagleUnsupervised(sourceCache, targetCache, getConfig());
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


	public Thread getLearningThread() {
		return learningThread;
	}


	public void setLearningThread(Thread learningThread) {
		this.learningThread = learningThread;
	}


	public Mapping getLearnedMapping() {
		return learnedMapping;
	}


	public void setLearnedMapping(Mapping learnedMapping) {
		this.learnedMapping = learnedMapping;
	}


	public Config getConfig() {
		return config;
	}


	public void setConfig(Config config) {
		this.config = config;
	}

}
