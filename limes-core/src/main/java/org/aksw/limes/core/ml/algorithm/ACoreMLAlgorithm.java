package org.aksw.limes.core.ml.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NoSuchParameterException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jun 7, 2016
 */
public abstract class ACoreMLAlgorithm {

    protected List<LearningParameter> parameters = new ArrayList<>();

    protected Cache sourceCache;

    protected Cache targetCache;
    
    protected Configuration configuration;

    /**
     * @return the configuration
     */
    public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
     * Name of the core ML algorithm.
     *
     * @return Name of the core ML algorithm.
     */
    protected abstract String getName();

    /**
     * @return current core ML algorithm parameters and their default values
     */
    protected List<LearningParameter> getParameters() {
        return parameters;
    }
    
    /**
     * Set default ACoreMLAlgorithm parameters values
     */
    public abstract void setDefaultParameters();

    /**
     * Initialize the core ML algorithm.
     *
     * @param lp learning parameters
     * @param sourceCache the source cache
     * @param targetCache the target cache
     */
    protected void init(List<LearningParameter> lp, Cache sourceCache, Cache targetCache) {
        if (lp != null) {
            this.parameters.addAll(lp);
        }
        this.sourceCache = sourceCache;
        this.targetCache = targetCache;
    }

    /**
     * Learning method for supervised core ML algorithm implementations, where
     * the confidence values for each pair in the trainingData determine its
     * truth degree.
     *
     * @param trainingData
     * @return wrap with results
     */
    protected abstract MLResults learn(AMapping trainingData)
            throws UnsupportedMLImplementationException;

    /**
     * Learning method for unsupervised core ML algorithm implementations.
     *
     * @param pfm
     * @return wrap with results
     * @throws UnsupportedMLImplementationException
     */
    protected abstract MLResults learn(PseudoFMeasure pfm)
            throws UnsupportedMLImplementationException;

    /**
     * Predict/generate links from source to target based on mlModel.
     *
     * @param source
     * @param target
     * @param mlModel
     * @return the mapping
     */
    protected abstract AMapping predict(Cache source, Cache target,
                                        MLResults mlModel);

    /**
     * Check whether the mlType is supported.
     *
     * @param mlType
     * @return a boolean value
     */
    protected abstract boolean supports(MLImplementationType mlType);

    /**
     * Get a set of examples to be added to the mapping.
     *
     * @param size
     * @return the mapping
     * @throws UnsupportedMLImplementationException
     */
    protected abstract AMapping getNextExamples(int size)
            throws UnsupportedMLImplementationException;

    /**
     * Learning method for supervised active core ML algorithm implementations.
     *
     * @param oracleMapping mapping from the oracle
     * @return wrap with results
     */
    protected abstract MLResults activeLearn(AMapping oracleMapping)
            throws UnsupportedMLImplementationException;
    
    /**
     * Learning method for supervised active core ML algorithm implementations
     * Normally, it is used as a first step to initialize the ML model 
     * before going through the active learning process
     * 
     * @return wrap with results
     * @throws UnsupportedMLImplementationException
     */
    protected abstract MLResults activeLearn() throws UnsupportedMLImplementationException;

    
    /**
     * Get parameter by name.
     * 
     * @param name parameter name
     * @return the parameter as Object
     * @throws NoSuchParameterException
     */
    protected Object getParameter(String name) {
    	for(LearningParameter par : parameters)
    		if(par.getName().equals(name))
    			return par.getValue();
    	return new NoSuchParameterException(name);
    }


}
