package org.aksw.limes.core.ml.algorithm.dragon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.ErrorEstimatePruning;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.PruningFunctionDTL;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;

/**
 * This class uses decision trees and an active learning approach to learn link
 * specifications
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class Dragon extends ACoreMLAlgorithm {
	/**
	 * delta Mappings that have already been calculated <deltaLS.toString(),
	 * mapping>
	 */
	private HashMap<String, AMapping> alreadyCalculatedMapping;

	static Logger logger = Logger.getLogger(Dragon.class);

	private PropertyMapping propertyMapping;
	private MLResults mlresult;
	/**
	 * LinkSpec with the subtracted delta threshold
	 */
	private LinkSpecification deltaLS;
	public TreeParser tp;
	private Configuration configuration;
	/**
	 * Candidates which the user has labeles
	 */
	private AMapping previouslyPresentedCandidates;
	/**
	 * the most certain candidates
	 */
	private ArrayList<SourceTargetValue> base;

	/**
	 * Since the most informative links are the ones near the boundary, where
	 * the instance pairs are being classified as links or not, we need to shift
	 * the threshold of the measure so we can also get the instance pairs that
	 * are almost classified as links
	 */
	private static final double delta = -0.1;

	// Parameters
	public static final String PARAMETER_UNPRUNED_TREE = "use unpruned tree";
	public static final String PARAMETER_COLLAPSE_TREE = "collapse tree";
	public static final String PARAMETER_PRUNING_CONFIDENCE = "confidence threshold for pruning";
	public static final String PARAMETER_REDUCED_ERROR_PRUNING = "reduced error pruning";
	public static final String PARAMETER_FOLD_NUMBER = "number of folds for reduced error pruning";
	public static final String PARAMETER_SUBTREE_RAISING = "perform subtree raising";
	public static final String PARAMETER_CLEAN_UP = "clean up after building tree";
	public static final String PARAMETER_LAPLACE_SMOOTHING = "laplace smoothing for predicted probabilities";
	public static final String PARAMETER_MDL_CORRECTION = "MDL correction for predicted probabilities";
	public static final String PARAMETER_SEED = "seed for random data shuffling";
	public static final String PARAMETER_PROPERTY_MAPPING = "property mapping";
	public static final String PARAMETER_MAPPING = "initial mapping as training data";
	public static final String PARAMETER_LINK_SPECIFICATION = "initial link specification to start training";
	public static final String PARAMETER_MAX_LINK_SPEC_HEIGHT = "maximum height of the link specification";
	public static final String PARAMETER_MIN_PROPERTY_COVERAGE = "minimum property coverage";
	public static final String PARAMETER_PROPERTY_LEARNING_RATE = "property learning rate";
	public static final String PARAMETER_FITNESS_FUNCTION = "fitness function";
	public static final String PARAMETER_PRUNING_FUNCTION = "pruning function";

	// Default parameters
	private static final boolean unprunedTree = false;
	private static final boolean collapseTree = true;
	private static final double pruningConfidence = 0.25;
	private static final boolean reducedErrorPruning = false;
	private static final int foldNumber = 3;
	private static final boolean subtreeRaising = true;
	private static final boolean cleanUp = true;
	private static final boolean laplaceSmoothing = false;
	private static final boolean mdlCorrection = true;
	private static final int seed = 1;
	private static final int maxLinkSpecHeight = 1;
	private static final double minPropertyCoverage = 0.6;
	private static final double propertyLearningRate = 0.95;
	private static final FitnessFunctionDTL fitnessFunction = new GiniIndex();
	private static final PruningFunctionDTL pruningFunction = new ErrorEstimatePruning();
	private AMapping initialMapping = MappingFactory.createDefaultMapping();
	private LinkSpecification bestLS;
	private double bestFMeasure;
	private AMapping prediction;
	public DecisionTree root;
	
	private ACache testSourceCache = new MemoryCache();
	private ACache testTargetCache = new MemoryCache();
	
	// TODO check whats wrong with exactmatch and levenshtein
	public static final String[] stringMeasures = { "cosine",
			// "exactmatch",
			"jaccard", "jaro",
			// "levenshtein",
			"qgrams", "trigrams" };
	public static final String[] dateMeasures = { "datesim", "daysim", "yearsim" };
	public static final String[] pointsetMeasures = { "symmetrichausdorff", "frechet", "hausdorff", "geolink",
			"geomean", "geolink", "surjection", "fairsurjection" };

	public static final String[] defaultMeasures = { "jaccard", "trigrams", "cosine", "qgrams" };
	// public static final String[] numberMeasures = {};

	public static final double threshold = 0.01;

	/**
	 * Constructor uses superconstructor and initializes TreeParser object
	 */
	public Dragon() {
		super();
		this.tp = new TreeParser(this);
	}

	/**
	 * Constructor uses superconstructor, initializes TreeParser object and sets
	 * configuration
	 * 
	 * @param c
	 */
	public Dragon(Configuration c) {
		super();
		this.configuration = c;
		this.tp = new TreeParser(this);
	}


	/**
	 * Helper class for easier handling of links or link candidates
	 * 
	 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
	 *         studserv.uni-leipzig.de{@literal >}
	 *
	 */
	public class SourceTargetValue {
		String sourceUri;
		String targetUri;
		double value;
		Double compoundMeasureValue = Double.MAX_VALUE;

		public SourceTargetValue(String s, String t, double v) {
			sourceUri = s;
			targetUri = t;
			value = v;
		}

		public SourceTargetValue(String s, String t, double v, Double cmv) {
			sourceUri = s;
			targetUri = t;
			value = v;
			compoundMeasureValue = cmv;
		}

		@Override
		public String toString() {
			return sourceUri + " -> " + targetUri + " : " + value + "     | compound measure value: "
					+ compoundMeasureValue;
		}
	}

	@Override
	public String getName() {
		return "Decision Tree Learning";
	}

	/**
	 * generates an initial training set and calls
	 * {@link #activeLearn(AMapping)}
	 */
	@Override
	protected MLResults activeLearn() throws UnsupportedMLImplementationException {
//		trainingData = getTrainingMapping();
//		if (trainingData == null) {
//			return null;
//		}
//		return activeLearn(trainingData);
		throw new UnsupportedMLImplementationException("Not implemented yet!");
	}

	/**
	 * Creates a training set out of the oracleMapping and uses {@link J48} to
	 * build a decision tree The decision tree gets parsed to a
	 * {@link LinkSpecification} by {@link TreeParser}
	 * 
	 * @param oracleMapping
	 * @return res wrapper containing learned link specification
	 */
	@Override
	protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
		if (oracleMapping.size() == 0) {
			logger.error("empty oracle Mapping! Returning empty MLResults!");
			return new MLResults();
		}
		// Add all previously labeled matches
		oracleMapping = addAll(oracleMapping);
		// These are the instances labeled by the user so we keep them to not
		// present the same pairs twice
		oracleMapping.getMap().forEach((sourceURI, map2) -> {
			map2.forEach((targetURI, value) -> {
				previouslyPresentedCandidates.add(sourceURI, targetURI, value);
			});
		});
		MLResults tmpMLResult = learn(oracleMapping);
		DecisionTree tree = (DecisionTree)tmpMLResult.getDetails().get("tree");
		AMapping treeMapping = tree.getTotalMapping();
		logger.info("treeMapping size: " + treeMapping.size());
		tmpMLResult.setQuality(tree.calculateFMeasure(treeMapping, oracleMapping));
		if(bestLS == null){
			this.mlresult = tmpMLResult;
			bestLS = tmpMLResult.getLinkSpecification();
			bestFMeasure = tmpMLResult.getQuality();
            deltaLS = subtractDeltaFromLS(this.mlresult.getLinkSpecification());
		}else{
			if(tmpMLResult.getQuality() > bestFMeasure){ 
				this.mlresult = tmpMLResult;
                bestLS = tmpMLResult.getLinkSpecification();
                bestFMeasure = tmpMLResult.getQuality();
                deltaLS = subtractDeltaFromLS(this.mlresult.getLinkSpecification());
			}
		}
		logger.info("\n\n === bestFM == \n : " + bestFMeasure);
		return this.mlresult;
	}


	/**
	 * Adds all {@link #previouslyPresentedCandidates} by calling union on the
	 * mappings
	 * 
	 * @param oracleMapping
	 * @return
	 */
	private AMapping addAll(AMapping oracleMapping) {
		return ((MemoryMapping) oracleMapping).union(previouslyPresentedCandidates);
	}


	/**
	 * calls wombat because it is designed to handle this case
	 * 
	 * @param oracleMapping
	 *            mapping containing user labeled data
	 * @return mlResult containing the result
	 * @throws UnsupportedMLImplementationException
	 */
	public MLResults handleUniformTrainingData(AMapping oracleMapping) throws UnsupportedMLImplementationException {
		logger.info("Training Data contains only positive/negative examples. Using Wombat");
		ActiveMLAlgorithm wombatSimpleA = null;
		try {
			wombatSimpleA = MLAlgorithmFactory
					.createMLAlgorithm(WombatSimple.class, MLImplementationType.SUPERVISED_ACTIVE).asActive();
			wombatSimpleA.init(null, sourceCache, targetCache);
			wombatSimpleA.activeLearn();
			this.mlresult = wombatSimpleA.activeLearn(oracleMapping);
			deltaLS = subtractDeltaFromLS(this.mlresult.getLinkSpecification());
			return this.mlresult;
		} catch (UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AMapping predict(ACache source, ACache target, MLResults mlModel) {
		LinkSpecification ls = mlModel.getLinkSpecification();
		Rewriter rw = RewriterFactory.getDefaultRewriter();
		ls = rw.rewrite(ls);
		DynamicPlanner dp = new DynamicPlanner(source, target);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target,
				this.configuration.getSourceInfo().getVar(), this.configuration.getTargetInfo().getVar());
		this.prediction = ee.execute(ls, dp);
		return this.prediction;
	}

	@Override
	public void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
		super.init(lp, sourceCache, targetCache);
		this.previouslyPresentedCandidates = MappingFactory.createDefaultMapping();
		this.base = new ArrayList<SourceTargetValue>();
		this.bestLS = null;
		this.prediction = null;
		alreadyCalculatedMapping = new HashMap<String, AMapping>();
		if (lp == null) {
			setDefaultParameters();
		}
	}

	/**
	 * Adds the delta shifting of the thresholds in the measures, checks if the
	 * operator is Minus in which case the threshold has to be raised
	 * 
	 * @param ls
	 *            LinkSpec to be cleaned of the delta shift
	 * @return cleaned LinkSpec
	 */
	private LinkSpecification subtractDeltaFromLS(LinkSpecification ls) {
		LinkSpecification lsClone = ls.clone();
		if (lsClone.getMeasure().startsWith("MINUS")) {
			return manipulateThreshold(lsClone, true, delta);
		}
		return manipulateThreshold(lsClone, false, delta);
	}


	/**
	 * Shifts the thresholds of the atomic measures in the link specification.
	 * If the threshold given is negative it gets subtracted, else it gets added
	 * On a MINUS operator its alway the other way round
	 * 
	 * @param ls
	 *            LinkSpec to be cleaned of the delta shift
	 * @param parentIsMinus
	 *            true if measure is minus
	 * @param threshold
	 *            if negative subtracted, if positive added to atomic measures
	 * @return cleaned LinkSpec
	 */
	private LinkSpecification manipulateThreshold(LinkSpecification ls, boolean measureIsMinus, double threshold) {
		if (ls.isAtomic()) {
			if (!measureIsMinus) {
				// have to use BigDecimal because of floating numbers magic
				double newThreshold = Math.min(1.0,
						(BigDecimal.valueOf(ls.getThreshold()).add(BigDecimal.valueOf(threshold))).doubleValue());
				// minimum threshold is 0.1
				newThreshold = Math.max(0.1, newThreshold);
				ls.setThreshold(newThreshold);
				return ls;
			} else {
				if (ls.getThreshold() == 0.0) {
					return ls;
				}
				double newThreshold = Math.max(0.1,
						(BigDecimal.valueOf(ls.getThreshold()).subtract(BigDecimal.valueOf(threshold))).doubleValue());
				// minimum threshold is 0.1
				newThreshold = Math.max(0.1, newThreshold);
				ls.setThreshold(newThreshold);
				return ls;
			}
		}
		ArrayList<LinkSpecification> newChildren = new ArrayList<LinkSpecification>();
		if (ls.getMeasure().startsWith("MINUS")) {
			for (LinkSpecification l : ls.getChildren()) {
				newChildren.add(manipulateThreshold(l, true, threshold));
			}
			ls.setChildren(newChildren);
		} else {
			for (LinkSpecification l : ls.getChildren()) {
				newChildren.add(manipulateThreshold(l, false, threshold));
			}
			ls.setChildren(newChildren);
		}
		return ls;
	}

	/**
	 * returns the {@link #bestLS}
	 * 
	 * @return
	 */
	public LinkSpecification getDefaultLS() {
		return bestLS;
	}

	@Override
	public void setDefaultParameters() {
		learningParameters = new ArrayList<>();
		learningParameters.add(new LearningParameter(PARAMETER_UNPRUNED_TREE, unprunedTree, Boolean.class, 0, 1, 0,
				PARAMETER_UNPRUNED_TREE));
		learningParameters.add(new LearningParameter(PARAMETER_COLLAPSE_TREE, collapseTree, Boolean.class, 0, 1, 1,
				PARAMETER_COLLAPSE_TREE));
		learningParameters.add(new LearningParameter(PARAMETER_PRUNING_CONFIDENCE, pruningConfidence, Double.class, 0d,
				1d, 0.01d, PARAMETER_PRUNING_CONFIDENCE));
		learningParameters.add(new LearningParameter(PARAMETER_REDUCED_ERROR_PRUNING, reducedErrorPruning,
				Boolean.class, 0, 1, 0, PARAMETER_REDUCED_ERROR_PRUNING));
		learningParameters.add(new LearningParameter(PARAMETER_FOLD_NUMBER, foldNumber, Integer.class, 0, 10, 1,
				PARAMETER_FOLD_NUMBER));
		learningParameters.add(new LearningParameter(PARAMETER_SUBTREE_RAISING, subtreeRaising, Boolean.class, 0, 1, 0,
				PARAMETER_SUBTREE_RAISING));
		learningParameters
				.add(new LearningParameter(PARAMETER_CLEAN_UP, cleanUp, Boolean.class, 0, 1, 0, PARAMETER_CLEAN_UP));
		learningParameters.add(new LearningParameter(PARAMETER_LAPLACE_SMOOTHING, laplaceSmoothing, Boolean.class, 0, 1,
				0, PARAMETER_LAPLACE_SMOOTHING));
		learningParameters.add(new LearningParameter(PARAMETER_MDL_CORRECTION, mdlCorrection, Boolean.class, 0, 1, 0,
				PARAMETER_MDL_CORRECTION));
		learningParameters.add(new LearningParameter(PARAMETER_SEED, seed, Integer.class, 0, 100, 1, PARAMETER_SEED));
		learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_MAPPING, propertyMapping, PropertyMapping.class,
				Double.NaN, Double.NaN, Double.NaN, PARAMETER_PROPERTY_MAPPING));
		learningParameters.add(new LearningParameter(PARAMETER_MAPPING, initialMapping, AMapping.class, Double.NaN,
				Double.NaN, Double.NaN, PARAMETER_MAPPING));
		learningParameters.add(new LearningParameter(PARAMETER_LINK_SPECIFICATION, bestLS, LinkSpecification.class,
				Double.NaN, Double.NaN, Double.NaN, PARAMETER_LINK_SPECIFICATION));
		learningParameters.add(new LearningParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT, maxLinkSpecHeight, Integer.class,
				1, 100000, 1, PARAMETER_MAX_LINK_SPEC_HEIGHT));
		learningParameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class,
				0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
		learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,
				Double.class, 0d, 1d, 0.01d, PARAMETER_PROPERTY_LEARNING_RATE));
		learningParameters.add(new LearningParameter(PARAMETER_FITNESS_FUNCTION, fitnessFunction,
				FitnessFunctionDTL.class, Double.NaN, Double.NaN, Double.NaN, PARAMETER_FITNESS_FUNCTION));
		learningParameters.add(new LearningParameter(PARAMETER_PRUNING_FUNCTION, pruningFunction,
				PruningFunctionDTL.class, Double.NaN, Double.NaN, Double.NaN, PARAMETER_PRUNING_FUNCTION));
	}

	@Override
	protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
		// root = new UnsupervisedDecisionTree(this, sourceCache, targetCache,
		// pfm,(double)getParameter(PARAMETER_MIN_PROPERTY_COVERAGE),
		// (double)getParameter(PARAMETER_PROPERTY_LEARNING_RATE));
		// UnsupervisedDecisionTree.maxDepth =
		// (int)getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT);
		// root.buildTree((int)getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT));
		// System.out.println(root.toString());
		// root.prune();
		// System.out.println(root.toString());
		// LinkSpecification ls = tp.parseTreePrefix(root.toString());
		// MLResults res = new MLResults(ls,
		// UnsupervisedDecisionTree.getTotalMapping(root), -1.0, null);
		// return res;
		logger.error("Not implemented yet!");
		return null;
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		return mlType == MLImplementationType.SUPERVISED_ACTIVE || mlType == MLImplementationType.SUPERVISED_BATCH
				|| mlType == MLImplementationType.UNSUPERVISED;
	}

	/**
	 * Executes the {@link #deltaLS} and calculates the compound measure for
	 * each instance pair in the resulting mapping compound measure is sum of
	 * |measure(s,t) - threshold of used measure|^2
	 */
	@Override
	protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		logger.info("Getting next examples to present user...");
		if (size == 0) {
			logger.error("next example size is 0! Returning empty mapping!);");
			return MappingFactory.createDefaultMapping();
		}
		AMapping deltaMapping = alreadyCalculatedMapping.get(deltaLS.toString());
		if (deltaMapping == null) {
			DynamicPlanner dp = new DynamicPlanner(this.sourceCache, this.targetCache);
			SimpleExecutionEngine ee = new SimpleExecutionEngine(this.sourceCache, this.targetCache,
					this.configuration.getSourceInfo().getVar(), this.configuration.getTargetInfo().getVar());
			deltaMapping = ee.execute(deltaLS, dp);
			alreadyCalculatedMapping.put(deltaLS.toString(), deltaMapping);
		} else {
			logger.debug("Skip execution because we already calculated this mapping");
		}
		ArrayList<SourceTargetValue> tmpCandidateList = new ArrayList<SourceTargetValue>();
		HashMap<String, Double> usedMeasures = this.getUsedMeasures(bestLS);
		deltaMapping.getMap().forEach((sourceURI, map2) -> {
			map2.forEach((targetURI, value) -> {
				double compoundMeasureValue = 0;
				for (Map.Entry<String, Double> entry : usedMeasures.entrySet()) {
					compoundMeasureValue += Math
							.pow(Math.abs(MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI),
									targetCache.getInstance(targetURI), entry.getKey(), threshold, "?x", "?y")
									- entry.getValue()), 2);
				}
				SourceTargetValue candidate = new SourceTargetValue(sourceURI, targetURI, value, compoundMeasureValue);
				if (!previouslyPresentedCandidates.contains(sourceURI, targetURI)) {
					tmpCandidateList.add(candidate);
				}
			});
		});

		tmpCandidateList.sort((s1, s2) -> s1.compoundMeasureValue.compareTo(s2.compoundMeasureValue));

		AMapping mostInformativeLinkCandidates = MappingFactory.createDefaultMapping();

		if (tmpCandidateList.size() != 0) {
			// Prevent IndexOutOfBoundException
			size = (tmpCandidateList.size() < size) ? tmpCandidateList.size() : size;
			// Add to mostInformativeLinkCandidates
			for (int i = 0; i < size; i++) {
				SourceTargetValue candidate = tmpCandidateList.get(i);
				mostInformativeLinkCandidates.add(candidate.sourceUri, candidate.targetUri, candidate.value);
			}
			// Add to base
			for (int i = 1; i <= size; i++) {
				SourceTargetValue candidate = tmpCandidateList.get(tmpCandidateList.size() - i);
				candidate.value = 1.0;
				if (!base.contains(candidate)) {
					base.add(candidate);
				}
			}
		} else {
			logger.info("All Candidates have been presented. Returning random unseen link candidates");
//			base.sort((s1, s2) -> s1.compoundMeasureValue.compareTo(s2.compoundMeasureValue));
//			size = (base.size() < size) ? base.size() : size;
//			for (int i = 0; i < size; i++) {
//				SourceTargetValue candidate = base.get(i);
//				mostInformativeLinkCandidates.add(candidate.sourceUri, candidate.targetUri, candidate.value);
//			}
			outerloop:
            for(Instance s: sourceCache.getAllInstances()){
                for(Instance t: targetCache.getAllInstances()){
                    if(!previouslyPresentedCandidates.contains(s.getUri(),t.getUri())){
                        mostInformativeLinkCandidates.add(s.getUri(), t.getUri(), 0.0);
                        if(mostInformativeLinkCandidates.size() >= size)
                        	break outerloop;
                    }
                }
            }
		}
		logger.info(mostInformativeLinkCandidates.size());
		return mostInformativeLinkCandidates;
	}

	/**
	 * Returns all used measures in the LinkSpecification with the corresponding
	 * highest threshold
	 * 
	 * @param ls
	 * @return HashMap with measure string as key and highest threshold as value
	 */
	private HashMap<String, Double> getUsedMeasures(LinkSpecification ls) {
		HashMap<String, Double> usedMeasures = new HashMap<String, Double>();
		if (ls.isAtomic()) {
			Double thresholdDouble = usedMeasures.get(ls.getAtomicMeasure());
			if (thresholdDouble != null) {
				if (usedMeasures.get(ls.getMeasure()) > thresholdDouble) {
					usedMeasures.put(ls.getMeasure(), thresholdDouble);
				}
			} else {
				thresholdDouble = ls.getThreshold();
				usedMeasures.put(ls.getMeasure(), thresholdDouble);
			}
			return usedMeasures;
		}
		for (LinkSpecification c : ls.getChildren()) {
			usedMeasures.putAll(getUsedMeasures(c));
		}
		return usedMeasures;
	}

	@Override
	protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
        DecisionTree.isSupervised = true;
        root = new DecisionTree(this, sourceCache, targetCache, null,
                (double) getParameter(PARAMETER_MIN_PROPERTY_COVERAGE),
                (double) getParameter(PARAMETER_PROPERTY_LEARNING_RATE),
                (double) getParameter(PARAMETER_PRUNING_CONFIDENCE), trainingData);
        DecisionTree.fitnessFunction = (FitnessFunctionDTL) getParameter(PARAMETER_FITNESS_FUNCTION);
        DecisionTree.pruningFunction = (PruningFunctionDTL) getParameter(PARAMETER_PRUNING_FUNCTION);
        DecisionTree.fitnessFunction.setDt(root);
        DecisionTree.maxDepth = (int) getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT);
        root.buildTree((int) getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT));
        logger.info("FULL:\n" + root.toString());

        root.prune();
        logger.info("PRUNED:\n" + root.toString());

        LinkSpecification ls = root.getTotalLS();
        HashMap<String, Object> details = new HashMap<>();
        details.put("tree",root);
        MLResults res = new MLResults(ls, null, -1.0, details);
        return res;
	}

	public ACache getSourceCache() {
		return this.sourceCache;
	}

	public ACache getTargetCache() {
		return this.targetCache;
	}

	public void setSourceCache(ACache sourceCache) {
		this.sourceCache = sourceCache;
	}

	public void setTargetCache(ACache targetCache) {
		this.targetCache = targetCache;
	}

	public PropertyMapping getPropertyMapping() {
		return propertyMapping;
	}

	public void setPropertyMapping(PropertyMapping propertyMapping) {
		this.propertyMapping = propertyMapping;
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public MLResults getMlresult() {
		return mlresult;
	}

	public void setInitialMapping(AMapping initialMapping) {
		this.initialMapping = initialMapping;
	}

	public ACache getTestSourceCache() {
		return testSourceCache;
	}

	public ACache getTestTargetCache() {
		return testTargetCache;
	}

}
