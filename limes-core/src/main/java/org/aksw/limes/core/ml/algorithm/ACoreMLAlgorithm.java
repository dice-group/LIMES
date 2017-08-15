package org.aksw.limes.core.ml.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NoSuchParameterException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public abstract class ACoreMLAlgorithm {
    protected static Logger logger = LoggerFactory.getLogger(ACoreMLAlgorithm.class);


    protected List<LearningParameter> learningParameters = new ArrayList<>();

    protected ACache sourceCache;

    protected ACache targetCache;
    
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
        return learningParameters;
    }
    
    /**
     * Set default ACoreMLAlgorithm parameters values
     */
    public abstract void setDefaultParameters();

    /**
     * Initialize the core ML algorithm.
     *
     * @param learningParameters learning parameters
     * @param sourceCache the source cache
     * @param targetCache the target cache
     */
    protected void init(List<LearningParameter> learningParameters, ACache sourceCache, ACache targetCache) {
        if (learningParameters != null) {
            //only update existing parameters
            for(LearningParameter lp : learningParameters){ 
                setParameter(lp.getName(), lp.getValue());
            }
        }
        this.sourceCache = sourceCache;
        this.targetCache = targetCache;
    }

    /**
     * Learning method for supervised core ML algorithm implementations, where
     * the confidence values for each pair in the trainingData determine its
     * truth degree.
     *
     * @param trainingData used for learning
     * @return wrap with results
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
    protected abstract MLResults learn(AMapping trainingData)
            throws UnsupportedMLImplementationException;

    /**
     * Learning method for unsupervised core ML algorithm implementations.
     *
     * @param pfm pseudo F-measure for unsupervised learning
     * @return wrap with results
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
    protected abstract MLResults learn(PseudoFMeasure pfm)
            throws UnsupportedMLImplementationException;

    /**
     * Predict/generate links from source to target based on mlModel.
     *
     * @param source Cache
     * @param target Cache
     * @param mlModel result of training phase
     * @return the mapping from source to target
     */
    protected abstract AMapping predict(ACache source, ACache target,
                                        MLResults mlModel);

    /**
     * Check whether the mlType is supported.
     *
     * @param mlType machine learning implementation type
     * @return a boolean value
     */
    protected abstract boolean supports(MLImplementationType mlType);

    /**
     * Get a set of examples to be added to the mapping.
     *
     * @param size of the examples
     * @return the mapping
     * @throws UnsupportedMLImplementationException Exception
     */
    protected abstract AMapping getNextExamples(int size)
            throws UnsupportedMLImplementationException;

    /**
     * Learning method for supervised active core ML algorithm implementations.
     *
     * @param oracleMapping mapping from the oracle
     * @return wrap with results
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
    protected abstract MLResults activeLearn(AMapping oracleMapping)
            throws UnsupportedMLImplementationException;
    
    /**
     * Learning method for supervised active core ML algorithm implementations
     * Normally, it is used as a first step to initialize the ML model 
     * before going through the active learning process
     * 
     * @return wrap with results
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
    protected abstract MLResults activeLearn() throws UnsupportedMLImplementationException;

    
    /**
     * Get parameter by name.
     * 
     * @param name parameter name
     * @return the parameter as Object
     * @throws NoSuchParameterException if parameter is not exists
     */
    protected Object getParameter(String name) {
    	for(LearningParameter par : learningParameters)
    		if(par.getName().equals(name))
    			return par.getValue();
    	return new NoSuchParameterException(name);
    }
    
    
    /**
     * @param par parameter name
     * @param val parameter value
     */
    public void setParameter(String par, Object val) {
        for(LearningParameter lp : learningParameters)
            if(lp.getName().equals(par)) {
                lp.setValue(val);
                return;
            }
        // if not found
        throw new NoSuchParameterException(par);
    }

    /**
     * @return the source cache
     */
    public ACache getSourceCache() {
        return sourceCache;
    }

    /**
     * @return the target cache
     */
    public ACache getTargetCache() {
        return targetCache;
    }


}
