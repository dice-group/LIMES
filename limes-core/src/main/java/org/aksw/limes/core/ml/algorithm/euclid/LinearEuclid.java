package org.aksw.limes.core.ml.algorithm.euclid;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator.QMeasureType;

/**
 * Class wraps around EUCLIDs linear classifier to abide LIMES ml interface
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public class LinearEuclid extends ACoreMLAlgorithm{

	protected static final String ALGORITHM_NAME = MLAlgorithmFactory.EUCLID_LINEAR;
	
	/* EUCLIDs parameter */
	public static final String STRICT = "strict";
	public static final String ITERATIONS_MAX = "max_iterations";
	public static final String MIN_THRESHOLD = "min_threshold";
	public static final String KAPPA = "kappa";
	public static final String BETA = "beta";
	public static final String LEARNING_RATE = "learning_rate";
    public static final String MIN_COVERAGE = "min_coverage";
    
    /**The EUCLID implementation*/
    protected LinearSelfConfigurator lsc = null;
    
    
    @Override
    protected void init(List<LearningParameter> learningParameters, ACache sourceCache, ACache targetCache) {
    	setDefaultParameters();
    	super.init(learningParameters, sourceCache, targetCache);
    	lsc = new LinearSelfConfigurator(sourceCache, targetCache);
    }
	
	@Override
	protected String getName() {
		return ALGORITHM_NAME;
	}

	@Override
	public void setDefaultParameters() {
        learningParameters = new ArrayList<>();
        learningParameters.add(new LearningParameter(ITERATIONS_MAX, 5, Integer.class, 1, Integer.MAX_VALUE, 1, "Maximal number of iterations EUCLID tries to refine its results."));
    	learningParameters.add(new LearningParameter(BETA, 1.0, Double.class, 0d, 1d, Double.NaN, "Shifts F-Measures towards precision or recall"));
    	learningParameters.add(new LearningParameter(STRICT, true, Boolean.class, -1, 1, 2,  STRICT));
    	learningParameters.add(new LearningParameter(MIN_THRESHOLD, 0.1, Double.class, 0d, 1d, Double.NaN, MIN_THRESHOLD));
    	learningParameters.add(new LearningParameter(KAPPA, 0.8, Double.class, 0d, 1d, Double.NaN, KAPPA));
    	learningParameters.add(new LearningParameter(LEARNING_RATE, 0.125, Double.class, 0d, 1d, Double.NaN, "Step range of each LSs treshold"));
    	learningParameters.add(new LearningParameter(MIN_COVERAGE, 0.9, Double.class, 0d, 1d, Double.NaN, "Coverage percentage of a property over all instances"));
	}

	/**
	 * Common learning method for both supervised and unsupervised Euclids.
	 * @return MLResults object containing a (equivalent) LS to the Euclids optimization method,
	 * 	a mapping, and eventually further detailed results.
	 */
	protected MLResults learn() {
		// get initial classifiers
		List<SimpleClassifier> init_classifiers = lsc.getBestInitialClassifiers();
		List<SimpleClassifier> result_classifiers  = lsc.learnClassifer(init_classifiers);
		// compute results
		MLResults result = new MLResults();
		AMapping mapping = lsc.getMapping(result_classifiers);
		result.setMapping(mapping);
		result.setQuality(lsc.computeQuality(mapping));
		result.setLinkSpecification(lsc.getLinkSpecification(result_classifiers));
		result.setClassifiers(result_classifiers);
		for(int i = 0; i<result_classifiers.size(); i++) {
			result.addDetail(i+". Classifier ", result_classifiers.get(i));
			AMapping map = lsc.executeClassifier(result_classifiers.get(i), result_classifiers.get(i).getThreshold());
			result.addDetail(i+". Mapping.size= ", map.size());
		}	
		return result;
	}
	
	@Override
	protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
		configureEuclid(lsc);
		lsc.setPFMType(QMeasureType.SUPERVISED);
		lsc.setSupervisedBatch(trainingData);
		return learn();
	}

	@Override
	protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
		// first setup EUCLID
		configureEuclid(lsc);
		lsc.setPFMType(QMeasureType.UNSUPERVISED);
		return learn();
	}

	@Override
	protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
		assert (mlModel.classifiersSet());
		List<SimpleClassifier> classifiers = mlModel.getClassifiers();
		assert(classifiers.size()>0);
		LinearSelfConfigurator le = new LinearSelfConfigurator(source, target);
		configureEuclid(le);
		AMapping map = le.getMapping(classifiers);
		logger.info("Should predict with mlModel on Caches +"+source.size()+","+target.size()+"+ resulted in "+map.size()+" map.");
		return map;
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		if(mlType.equals(MLImplementationType.UNSUPERVISED) || mlType.equals(MLImplementationType.SUPERVISED_BATCH))
			return true;
		return false;
	}

	@Override
	protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException("Active Learning for Euclid is not implemented yet.");
	}

	@Override
	protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException("Active Learning for Euclid is not implemented yet.");
	}

	@Override
	protected MLResults activeLearn() throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException("Active Learning for Euclid is not implemented yet.");
	}

	/**
	 * To configure EUCLID implementation. Checks for new parameters and sets up EUCLID accordingly.
	 */
	protected void configureEuclid(LinearSelfConfigurator lsc) {
		if(lsc == null)
			lsc = new LinearSelfConfigurator(sourceCache, targetCache);
		double min_cov = (double) getParameter(MIN_COVERAGE);
		lsc.beta = (double) getParameter(BETA);
		if(Math.abs(lsc.min_coverage - min_cov) > 0.05) {
			lsc = new LinearSelfConfigurator(sourceCache, targetCache, lsc.beta, min_cov);
		}		
		lsc.ITERATIONS_MAX = (int) getParameter(ITERATIONS_MAX);
		lsc.MIN_THRESHOLD = (double) getParameter(MIN_THRESHOLD);
		lsc.kappa = (double) getParameter(KAPPA);
		lsc.learningRate = (double) getParameter(LEARNING_RATE);
		boolean strict = (boolean) getParameter(STRICT);
		lsc.STRICT = strict;
		
	}
	
}
