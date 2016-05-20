package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;

public abstract class ACoreMLAlgorithm {
	
	/**
	 * Name of the core ML algorithm.
	 * 
	 * @return
	 */
	protected abstract String getName();
	
	/**
	 * Initialize the core ML algorithm.
	 * 
	 * @param ls
	 * @param source
	 * @param target
	 */
	protected abstract void init(LearningSetting ls, Cache source, Cache target);
	
	/**
	 * Learning method for supervised core ML algorithm implementations.
	 * 
	 * @param trainingData
	 * @return
	 */
	protected abstract MLModel learn(Mapping trainingData) throws UnsupportedMLImplementationException;

	/**
	 * Learning method for unsupervised core ML algorithm implementations.
	 * 
	 * @param pfm
	 * @return
	 * @throws UnsupportedMLImplementationException 
	 */
	protected abstract MLModel learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException;

	/**
	 * Predict/generate links from source to target based on mlModel.
	 * 
	 * @param source
	 * @param target
	 * @param mlModel
	 * @return
	 */
	protected abstract Mapping predict(Cache source, Cache target, MLModel mlModel);
	
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
	protected abstract Mapping getNextExamples(int size) throws UnsupportedMLImplementationException;
	
	/**
	 * Learning method for supervised active core ML algorithm implementations.
	 * 
	 * @param oracleMapping
	 * @return
	 */
	protected abstract MLModel activeLearn(Mapping oracleMapping) throws UnsupportedMLImplementationException;
	
}
