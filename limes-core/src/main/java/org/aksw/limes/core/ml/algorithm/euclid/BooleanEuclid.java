package org.aksw.limes.core.ml.algorithm.euclid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;
import org.apache.log4j.Logger;

public class BooleanEuclid extends ACoreMLAlgorithm{

	protected static Logger logger = Logger.getLogger(BooleanEuclid.class);
	
	static final String ALGORITHM_NAME = "Euclid";
    // execution mode. STRICT = true leads to a strong bias towards precision by
    // ensuring that the initial classifiers are classifiers that have the
    // maximal threshold that leads to the best pseudo-f-measure. False leads to the
    // best classifier with the smallest threshold
    public boolean STRICT = true;
    public int ITERATIONS_MAX = 1000;
    public double MIN_THRESHOLD = 0.3;
    public double MAX_THRESHOLD = 1.0;

	@Override
	protected String getName() {
		return ALGORITHM_NAME;
	}

	@Override
	public void setDefaultParameters() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
		BooleanSelfConfigurator bsc = new BooleanSelfConfigurator(target, target, MIN_THRESHOLD, MIN_THRESHOLD);
		List<SimpleClassifier> simpleList = bsc.getBestInitialClassifiers();
//		bsc.
		return null;
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		return MLImplementationType.UNSUPERVISED == mlType;
	}

	@Override
	protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException("EUCLID active learning not implemented yet.");
	}

	@Override
	protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException("EUCLID active learning not implemented yet.");
	}

	@Override
	protected MLResults activeLearn() throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

}
