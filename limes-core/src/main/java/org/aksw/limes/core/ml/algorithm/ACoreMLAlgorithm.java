package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jun 7, 2016
 */
public abstract class ACoreMLAlgorithm {

    protected LearningParameters parameters = new LearningParameters();

    protected Cache sourceCache;

    protected Cache targetCache;

    /**
     * Name of the core ML algorithm.
     *
     * @return
     */
    protected abstract String getName();

    /**
     * @return current core ML algorithm parameters and their default values
     */
    protected LearningParameters getParameters() {
        return parameters;
    }
    
    /**
     * Set default ACoreMLAlgorithm parameters values
     */
    protected abstract void setDefaultParameters();

    /**
     * Initialize the core ML algorithm.
     *
     * @param ls
     * @param sourceCache
     * @param targetCache
     */
    protected void init(LearningParameters lp, Cache sourceCache, Cache targetCache) {
        if (lp != null && !lp.isEmpty()) {
            this.parameters.putAll(lp);
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
     * @return
     */
    protected abstract MLModel learn(AMapping trainingData)
            throws UnsupportedMLImplementationException;

    /**
     * Learning method for unsupervised core ML algorithm implementations.
     *
     * @param pfm
     * @return
     * @throws UnsupportedMLImplementationException
     */
    protected abstract MLModel learn(PseudoFMeasure pfm)
            throws UnsupportedMLImplementationException;

    /**
     * Predict/generate links from source to target based on mlModel.
     *
     * @param source
     * @param target
     * @param mlModel
     * @return
     */
    protected abstract AMapping predict(Cache source, Cache target,
                                        MLModel mlModel);

    /**
     * Check whether the mlType is supported.
     *
     * @param mlType
     * @return
     */
    protected abstract boolean supports(MLImplementationType mlType);

    /**
     * Get a set of examples to be added to the mapping.
     *
     * @param size
     * @return
     * @throws UnsupportedMLImplementationException
     */
    protected abstract AMapping getNextExamples(int size)
            throws UnsupportedMLImplementationException;

    /**
     * Learning method for supervised active core ML algorithm implementations.
     *
     * @param oracleMapping
     * @return
     */
    protected abstract MLModel activeLearn(AMapping oracleMapping)
            throws UnsupportedMLImplementationException;
    
    /**
     * Learning method for supervised active core ML algorithm implementations
     * Normally, it is used as a first step to train the ML model in unsupervised 
     * way before going through the active learning process
     * 
     * @return
     * @throws UnsupportedMLImplementationException
     */
    protected abstract MLModel activeLearn() throws UnsupportedMLImplementationException;

}
