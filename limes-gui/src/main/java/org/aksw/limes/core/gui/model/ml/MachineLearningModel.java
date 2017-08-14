package org.aksw.limes.core.gui.model.ml;

import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;

/**
 * this class is responsible for the data handling according to the MVC Pattern for the machine learning
 *  
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public abstract class MachineLearningModel {


    /**
     * logger for this class
     */
    protected static Logger logger = LoggerFactory.getLogger("LIMES");
    /**
     * algorithm
     */
    protected AMLAlgorithm mlalgorithm;
    /**
     * parameters
     */
    protected List<LearningParameter> learningParameters;
    /**
     * sourceCache
     */
    protected ACache sourceCache;
    /**
     * targetCache
     */
    protected ACache targetCache;
    /**
     * thread in which the learning is done
     */
    protected Thread learningThread;
    /**
     * configuration
     */
    protected Config config;
    /**
     * the resulting mapping of a learning process
     */
    protected AMapping learnedMapping;
    /**
     * the learned LinkSpecification
     */
    protected LinkSpecification learnedLS;

    /**
     * constructor
     * @param config contains the information
     * @param sourceCache source
     * @param targetCache target
     */
    public MachineLearningModel(Config config, ACache sourceCache, ACache targetCache) {
        this.setConfig(config);
        this.sourceCache = sourceCache;
        this.targetCache = targetCache;
    }


    /**
     * creates the learning task for this algorithm 
     * @return the task
     */
    public abstract Task<Void> createLearningTask();


    /**
     * return algorithm
     * @return the algorithm
     */
    public AMLAlgorithm getMlalgorithm() {
        return mlalgorithm;
    }

    /**
     * set algorithm
     * @param mlalgorithm the algorithm to be set
     */
    public void setMlalgorithm(AMLAlgorithm mlalgorithm) {
        this.mlalgorithm = mlalgorithm;
    }

    /**
     * return learning parameters
     * @return learningParameters
     */
    public List<LearningParameter> getLearningParameters() {
        return learningParameters;
    }

    /**
     * set learning parameters
     * @param learningParameters the parameters
     */
    public void setLearningParameters(List<LearningParameter> learningParameters) {
        this.learningParameters = learningParameters;
    }

    /**
     * creates a new mlalgorithm using {@link MLAlgorithmFactory}
     * @param algorithmName the algorithm name
     */
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
    }

    /**
     * return learning thread
     * @return the thread
     */
    public Thread getLearningThread() {
        return learningThread;
    }


    /**
     * set learning thread
     * @param learningThread th thread to be set
     */
    public void setLearningThread(Thread learningThread) {
        this.learningThread = learningThread;
    }


    /**
     * get learned mapping
     * @return the learned mapping
     */
    public AMapping getLearnedMapping() {
        return learnedMapping;
    }


    /**
     * set learnedMapping
     * @param learnedMapping learned mapping
     */
    public void setLearnedMapping(AMapping learnedMapping) {
        this.learnedMapping = learnedMapping;
    }


    /**
     * return config
     * @return the config
     */
    public Config getConfig() {
        return config;
    }


    /**
     * set config
     * @param config config
     */
    public void setConfig(Config config) {
        this.config = config;
    }


    /**
     * get learnedLS
     * @return learnedLS
     */
    public LinkSpecification getLearnedLS() {
        return learnedLS;
    }


    /**
     * set learnedLS
     * @param learnedLS learnedLS
     */
    public void setLearnedLS(LinkSpecification learnedLS) {
        this.learnedLS = learnedLS;
    }

}
