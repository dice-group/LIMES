package org.aksw.limes.core.gui.model.ml;

import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.apache.log4j.Logger;

import javafx.concurrent.Task;

public abstract class MachineLearningModel {

//	protected MachineLearningView mlview;

    protected static Logger logger = Logger.getLogger("LIMES");
    protected AMLAlgorithm mlalgorithm;
    protected List<LearningParameter> learningParameters;
    protected Cache sourceCache;
    protected Cache targetCache;
    protected Thread learningThread;
    protected Config config;
    protected AMapping learnedMapping;

    public MachineLearningModel(Config config, Cache sourceCache, Cache targetCache) {
        this.setConfig(config);
        this.sourceCache = sourceCache;
        this.targetCache = targetCache;
    }


    public abstract Task<Void> createLearningTask();


    public AMLAlgorithm getMlalgorithm() {
        return mlalgorithm;
    }

    public void setMlalgorithm(AMLAlgorithm mlalgorithm) {
        this.mlalgorithm = mlalgorithm;
    }

    public List<LearningParameter> getLearningParameters() {
        return learningParameters;
    }

    public void setLearningParameters(List<LearningParameter> learningParameters) {
        this.learningParameters = learningParameters;
    }

    public void initializeData(String algorithmName) {
        //TODO other cases
	algorithmName = algorithmName.toLowerCase();
	String implementationTypeName = null;
        if (this instanceof ActiveLearningModel) {
            implementationTypeName = MLAlgorithmFactory.SUPERVISED_ACTIVE;
        } else if (this instanceof BatchLearningModel) {
            implementationTypeName = MLAlgorithmFactory.SUPERVISED_BATCH;
        } else if (this instanceof UnsupervisedLearningModel) {
            implementationTypeName = MLAlgorithmFactory.UNSUPERVISED;
        } else {
            logger.error("Unknown subclass of MachineLearningModel");
        }

	try {
	    this.mlalgorithm = MLAlgorithmFactory.createMLAlgorithm(MLAlgorithmFactory.getAlgorithmType(algorithmName), MLAlgorithmFactory.getImplementationType(implementationTypeName));
	} catch (UnsupportedMLImplementationException e) {
	    // TODO Auto-generated catch block
	    logger.error("Unsupported Machine Learning Implementation!");
	    e.printStackTrace();
	}
//        switch (algorithmName) {
//            case "Lion":
//                this.mlalgorithm = new Lion(sourceCache, targetCache, getConfig());
//                break;
//            case "Eagle":
//                if (this instanceof UnsupervisedLearningModel) {
//                    this.mlalgorithm = new EagleUnsupervised(sourceCache, targetCache, getConfig());
//                } else {
//                    logger.info("Not implemented yet");
//                }
//                break;
//            default:
//                logger.info("Unknown algorithm");
//        }
//        if (this instanceof ActiveLearningModel) {
//            this.learningsetting = new ActiveLearningSetting(mlalgorithm);
//        } else if (this instanceof BatchLearningModel) {
//            this.learningsetting = new BatchLearningSetting(mlalgorithm);
//        } else if (this instanceof UnsupervisedLearningModel) {
//            this.learningsetting = new UnsupervisedLearningSetting(mlalgorithm);
//        } else {
//            logger.info("Unknown subclass of MachineLearningModel");
//        }
    }

    public Thread getLearningThread() {
        return learningThread;
    }


    public void setLearningThread(Thread learningThread) {
        this.learningThread = learningThread;
    }


    public AMapping getLearnedMapping() {
        return learnedMapping;
    }


    public void setLearnedMapping(AMapping learnedMapping) {
        this.learnedMapping = learnedMapping;
    }


    public Config getConfig() {
        return config;
    }


    public void setConfig(Config config) {
        this.config = config;
    }

}
