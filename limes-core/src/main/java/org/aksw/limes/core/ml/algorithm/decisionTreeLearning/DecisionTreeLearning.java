package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.util.ParenthesisMatcher;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class uses decision trees and an active learning approach to learn link
 * specifications
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class DecisionTreeLearning extends ACoreMLAlgorithm {
	/**
	 * LinkSpecs that have already been checked, <ls.toString(), fmeasure value>
	 */
	private HashMap<String, Double> alreadySeenLS;
	/**
	 * delta Mappings that have already been calculated <deltaLS.toString(),
	 * mapping>
	 */
	private HashMap<String, AMapping> alreadyCalculatedMapping;

	static Logger logger = Logger.getLogger(DecisionTreeLearning.class);

	private PropertyMapping propertyMapping;
	/**
	 * weka instances
	 */
	private Instances trainingSet;
	private MLResults mlresult;
	/**
	 * LinkSpec with the subtracted delta threshold
	 */
	private LinkSpecification deltaLS;
	public TreeParser tp;
	private J48 tree;
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
	 * helper boolean for uniformTrainingData
	 */
	private boolean negativeExample = false;
	/**
	 * helper boolean for uniformTrainingData
	 */
	private boolean positiveExample = false;
	/**
	 * Since the most informative links are the ones near the boundary, where
	 * the instance pairs are being classified as links or not, we need to shift
	 * the threshold of the measure so we can also get the instance pairs that
	 * are almost classified as links
	 */
	private static final double delta = -0.1;

	/**
	 * if we have already seen a linkspecification we raise the threshold by
	 * this amount
	 */
	private static final double thresholdRaise = 0.05;

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
	private static final double minPropertyCoverage = 0.4; 
	private static final double propertyLearningRate = 0.9;
	private AMapping initialMapping = MappingFactory.createDefaultMapping();
	private LinkSpecification bestLS;
	private FMeasure fmeasure;
	private double bestFMeasure = 0.0;
	private AMapping prediction;
	private AMapping trainingData;
public UnsupervisedDecisionTree root;

	// TODO check whats wrong with these
	public static final String[] stringMeasures = { "cosine",
			// "exactmatch",
			"jaccard", "jaro",
			// "levenshtein",
			"qgrams", "trigrams" };
	public static final String[] dateMeasures = { "datesim", "daysim", "yearsim" };
	public static final String[] pointsetMeasures = { "symmetrichausdorff", "frechet", "hausdorff", "geolink",
			"geomean", "geolink", "surjection", "fairsurjection" };
	
	public static final String[] defaultMeasures = {"jaccard", "trigrams", "cosine", "qgrams"};
	// public static final String[] numberMeasures = {};

	public static final double threshold = 0.01;

	/**
	 * Constructor uses superconstructor and initializes TreeParser object
	 */
	public DecisionTreeLearning() {
		super();
		this.tp = new TreeParser(this);
	}

	/**
	 * Constructor uses superconstructor, initializes TreeParser object and sets
	 * configuration
	 * 
	 * @param c
	 */
	public DecisionTreeLearning(Configuration c) {
		super();
		this.configuration = c;
		this.tp = new TreeParser(this);
	}

	/**
	 * Generates training set out of config if there is a linkspec, else returns
	 * a random mapping
	 * 
	 * @return mapping of executed linkspec
	 */
	public AMapping getTrainingMapping() {
		logger.info("Getting initial training mapping...");
		AMapping mapping = MappingFactory.createDefaultMapping();
		if (this.initialMapping.size() > 0) {
			mapping = this.initialMapping;
			mapping = balanceInitialMapping(mapping);
		} else if (this.bestLS != null) {
			logger.info("...by running given LinkSpecification...");
			DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
			SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache,
					this.configuration.getSourceInfo().getVar(), this.configuration.getTargetInfo().getVar());
			mapping = ee.execute(bestLS, dp);
		} else if (this.configuration != null) {
			logger.info("...by running LinkSpecification from Configuration...");
			if (this.configuration.getMetricExpression() != null
					&& !this.configuration.getMetricExpression().isEmpty()) {
				bestLS = new LinkSpecification();
				bestLS.readSpec(this.configuration.getMetricExpression(), this.configuration.getAcceptanceThreshold());
				DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
				SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache,
						this.configuration.getSourceInfo().getVar(), this.configuration.getTargetInfo().getVar());
				mapping = ee.execute(bestLS, dp);
			}
		} else {
			logger.error("No initial mapping or linkspecification as parameter given! Returning null!");
			mapping = null;
		}
		return mapping;
	}

	/**
	 * Creates {@link Instances}, with attributes but does not fill them with
	 * values Attributes are of the form: measure delimiter propertyA |
	 * propertyB (without spaces)
	 * 
	 * @param mapping
	 *            will be used to create attributes
	 * @return trainingInstances
	 */
	private Instances createEmptyTrainingInstances(AMapping mapping) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (PairSimilar<String> propPair : propertyMapping.stringPropPairs) {
			for (String measure : stringMeasures) {
				Attribute attr = new Attribute(measure + TreeParser.delimiter + propPair.a + "|" + propPair.b);
				attributes.add(attr);
			}
		}
		for (PairSimilar<String> propPair : propertyMapping.datePropPairs) {
			for (String measure : dateMeasures) {
				Attribute attr = new Attribute(measure + TreeParser.delimiter + propPair.a + "|" + propPair.b);
				attributes.add(attr);
			}
		}
		for (PairSimilar<String> propPair : propertyMapping.pointsetPropPairs) {
			for (String measure : pointsetMeasures) {
				Attribute attr = new Attribute(measure + TreeParser.delimiter + propPair.a + "|" + propPair.b);
				attributes.add(attr);
			}
		}
		// TODO what about number properties????
		ArrayList<String> matchClass = new ArrayList<String>(2);
		matchClass.add("positive");
		matchClass.add("negative");
		Attribute match = new Attribute("match", matchClass);
		attributes.add(match);
		Instances trainingInstances = new Instances("link", attributes, mapping.size());
		trainingInstances.setClass(match);
		return trainingInstances;
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

	/**
	 * Fills every attribute (except the class attribute) of the weka Instances
	 * by running all similarity measures for properties of corresponding source
	 * and target Instances
	 * 
	 * @param trainingSet
	 * @param mapping
	 */
	private HashMap<Instance, SourceTargetValue> fillInstances(Instances trainingSet, AMapping mapping) {
		HashMap<Instance, SourceTargetValue> instanceMap = new HashMap<Instance, SourceTargetValue>();
		mapping.getMap().forEach((sourceURI, map2) -> {
			map2.forEach((targetURI, value) -> {
				Instance inst = new DenseInstance(trainingSet.numAttributes());
				inst.setDataset(trainingSet);
				for (int i = 0; i < trainingSet.numAttributes(); i++) {
					// class index has to be left empty
					if (i != trainingSet.classIndex()) {
						String[] measureAndProperties = tp.getMeasureAndProperties(trainingSet.attribute(i).name());
						String measureName = measureAndProperties[0];
						String propertyA = measureAndProperties[1];
						String propertyB = measureAndProperties[2];
						String metricExpression = measureName + "(x." + propertyA + ", y." + propertyB + ")";
						if (targetCache.getInstance(targetURI) == null || sourceCache.getInstance(sourceURI) == null) {
							// instances in caches are sometimes
							// surrounded by angle brackets
							if (sourceCache.getInstance("<" + sourceURI + ">") == null
									|| targetCache.getInstance("<" + targetURI + ">") == null) {
								if (sourceCache.getInstance("<" + sourceURI + ">") == null)
									logger.error("source null");
								if (targetCache.getInstance("<" + targetURI + ">") == null)
									logger.error("target null");
								logger.error(
										"URI from training mapping cannot be found in source/target cache.\n sourceURI: "
												+ sourceURI + " targetURI: " + targetURI + " : " + value);
							} else {
								inst.setValue(i,
										MeasureProcessor.getSimilarity(sourceCache.getInstance("<" + sourceURI + ">"),
												targetCache.getInstance("<" + targetURI + ">"), metricExpression,
												threshold, "?x", "?y"));
							}
						} else {
							inst.setValue(i, MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI),
									targetCache.getInstance(targetURI), metricExpression, threshold, "?x", "?y"));
							// System.out.println(sourceURI);
							// System.out.println(targetURI);
							// System.out.println(inst.attribute(i));
							// System.err.println(sourceCache.getInstance(sourceURI).getProperty(propertyA));
							// System.err.println(targetCache.getInstance(targetURI).getProperty(propertyB));
							// System.out.println(inst.value(i));
							// System.out.println(inst);
						}
					} else {
						String classValue = "negative";
						if (value > 0.9) {
							classValue = "positive";
							positiveExample = true;
						} else {
							negativeExample = true;
						}
						inst.setValue(i, classValue);
					}
				}
				instanceMap.put(inst, new SourceTargetValue(sourceURI, targetURI, value));
				trainingSet.add(inst);

			});
		});
		// System.out.println(trainingSet);
		return instanceMap;
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
		trainingData = getTrainingMapping();
		if (trainingData == null) {
			return null;
		}
		return activeLearn(trainingData);
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
		LinkSpecification resLS = buildTreeAndParseToLS(oracleMapping);
		if (resLS == null) {
			logger.info("Bad tree! Giving the algorithm more information by adding more instances.");
			if (addBase(oracleMapping)) {
				resLS = buildTreeAndParseToLS(oracleMapping);
			} else {
				// if adding the base does not help we call wombat
				handleUniformTrainingData(oracleMapping);
			}
		}
		LinkSpecification raisedLS = null;
		// If we get the same tree again, raise the threshold to get better
		// results faster
		while (alreadySeenLS.get(resLS.toString()) != null) {
			logger.debug("Already seen " + resLS);
			raisedLS = raiseThreshold(resLS);
			// they are the same if we reached the maximum threshold
			if (raisedLS.equals(resLS)) {
				break;
			} else {
				resLS = raisedLS;
			}
			logger.debug("Setting threshold to: " + resLS.getThreshold());
		}
		this.mlresult = new MLResults();
		if (checkIfThereWasBetterLSBefore(resLS)) {
			logger.debug("Already had better LinkSpecification: " + bestLS);
		} else {
			logger.debug("Learned LinkSpecification: " + resLS.toStringOneLine());
		}
		this.mlresult.setLinkSpecification(bestLS);
		this.mlresult.setMapping(prediction);
		this.mlresult.setQuality(bestFMeasure);
		deltaLS = subtractDeltaFromLS(resLS);
		return this.mlresult;
	}

	/**
	 * Builds the J48 tree and calls the parser to get the LinkSpecification
	 * 
	 * @param oracleMapping
	 * @return
	 */
	private LinkSpecification buildTreeAndParseToLS(AMapping oracleMapping) {
		this.trainingSet = createEmptyTrainingInstances(oracleMapping);
		fillInstances(trainingSet, oracleMapping);
		if (!(negativeExample & positiveExample)) {
			logger.debug("negative examples: " + negativeExample + " positive examples:" + positiveExample);
			negativeExample = false;
			positiveExample = false;
			return null;
		}
		LinkSpecification resLS = null;
		try {
			String[] options = getOptionsArray();
			tree = new J48();
			tree.setOptions(options);
			logger.info("Building classifier....");
			tree.buildClassifier(trainingSet);
			// System.out.println(tree.prefix());
			// System.out.println(tree.graph());
			if (tree.prefix().startsWith("[negative ") || tree.prefix().startsWith("[positive ")) {
				logger.info("Bad tree! Giving the algorithm more information by adding more instances.");
				if (addBase(oracleMapping)) {
					resLS = buildTreeAndParseToLS(oracleMapping);
				} else {
					handleUniformTrainingData(oracleMapping);
				}
			}
			logger.info("Parsing tree to LinkSpecification...");
			resLS = treeToLinkSpec(tree);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resLS;
	}

	/**
	 * Checks if there was already a better LinkSpecification before if this is
	 * batch learning it checks all thresholds above the learned also
	 * 
	 * @param ls
	 * @return
	 */
	private boolean checkIfThereWasBetterLSBefore(LinkSpecification ls) {
		while (alreadySeenLS.get(ls.toString()) == null && ls.getThreshold() != 1.0) {
			logger.debug("Checking: " + ls);
			MLResults tmp = new MLResults();
			tmp.setLinkSpecification(ls);
			AMapping pred = predict(this.sourceCache, this.targetCache, tmp);
			double pfresult = fmeasure.calculate(pred,
					new GoldStandard(trainingData, this.sourceCache.getAllUris(), this.targetCache.getAllUris()));
			logger.debug("best before: " + bestFMeasure + " now: " + pfresult);
			alreadySeenLS.put(ls.toString(), pfresult);
			if (pfresult > bestFMeasure) {
				this.prediction = pred;
				this.bestLS = ls;
				this.bestFMeasure = pfresult;
			}
			LinkSpecification raisedLS = raiseThreshold(ls);
			// they are the same if we reached the maximum threshold
			if (raisedLS.equals(ls)) {
				break;
			} else {
				ls = raisedLS;
			}
		}
		return true;
	}

	/**
	 * Adds {@link #base} to the oracle mapping, until positive and negative
	 * examples are equal
	 * 
	 * @param oracleMapping
	 * @return
	 */
	private boolean addBase(AMapping oracleMapping) {
		boolean changed = false;
		int positive = 0;
		int negative = 0;
		for (String s : oracleMapping.getMap().keySet()) {
			for (String t : oracleMapping.getMap().get(s).keySet()) {
				if (oracleMapping.getMap().get(s).get(t) == 1.0) {
					positive++;
				} else {
					negative++;
				}
			}
		}
		logger.debug("positive: " + positive + " negative: " + negative);
		if (base.size() != 0) {
			Iterator<SourceTargetValue> it = base.iterator();
			while (it.hasNext() && positive != negative) {
				SourceTargetValue inst = it.next();
				if (inst.value == 1.0) {
					positive++;
				} else {
					negative++;
				}
				if (!oracleMapping.contains(inst.sourceUri, inst.targetUri)) {
					oracleMapping.add(inst.sourceUri, inst.targetUri, inst.value);
					changed = true;
				}
			}
		}
		logger.debug("positive: " + positive + " negative: " + negative);
		return changed;
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
	 * Tries to get an equal amount of positive and negative examples. If
	 * positive == negative it just returns the mapping If positive < negative
	 * it deletes some negative examples. If negative < mapping.size / 4 it
	 * returns null, telling the user to provide more negative examples
	 * 
	 * @param oracleMapping
	 * @return
	 */
	private AMapping balanceInitialMapping(AMapping oracleMapping) {
		AMapping originalOracleMapping = MappingFactory.createDefaultMapping();
		int positive = 0;
		int negative = 0;
		for (String s : oracleMapping.getMap().keySet()) {
			for (String t : oracleMapping.getMap().get(s).keySet()) {
				double v = oracleMapping.getMap().get(s).get(t);
				if (v == 1.0) {
					originalOracleMapping.add(s, t, v);
					positive++;
				} else {
					originalOracleMapping.add(s, t, v);
					negative++;
				}
			}
		}
		logger.debug("Initial mapping contains  " + positive + " positive and  " + negative + " negative examples");
		if (positive == negative) {
			return oracleMapping;
		} else if (positive < negative) {
			Iterator<Entry<String, HashMap<String, Double>>> it = oracleMapping.getMap().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, HashMap<String, Double>> item = it.next();
				if (item.getValue().values().contains(0.0) && positive != negative) {
					it.remove();
					negative--;
				}
			}
		} else if (negative < (float) oracleMapping.size() / 4.0) {
			logger.error(
					"Please provide more negative examples! An equal amount of positive and negative examples works best");
			return null;
		}
		logger.debug("Balanced mapping contains  " + positive + " positive and  " + negative + " negative examples");
		return oracleMapping;
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

	/**
	 * Takes the options and puts them into a String[] so it can be used as
	 * options in {@link J48}
	 * 
	 * @return array with options
	 */
	private String[] getOptionsArray() {
		ArrayList<String> tmpOptions = new ArrayList<>();
		if ((boolean) getParameter(PARAMETER_UNPRUNED_TREE)) {
			tmpOptions.add("-U");
		}
		if (!((boolean) getParameter(PARAMETER_COLLAPSE_TREE))) {
			tmpOptions.add("-O");
		}
		// default value in J48 is 0.25
		if (((double) getParameter(PARAMETER_PRUNING_CONFIDENCE)) != 0.25) {
			tmpOptions.add("-C");
			tmpOptions.add(String.valueOf((double) getParameter(PARAMETER_PRUNING_CONFIDENCE)));
		}
		if ((boolean) getParameter(PARAMETER_REDUCED_ERROR_PRUNING)) {
			tmpOptions.add("-R");
		}
		// default value in J48 is 3
		if (((int) getParameter(PARAMETER_FOLD_NUMBER)) != 3) {
			tmpOptions.add("-N");
			tmpOptions.add(String.valueOf(((int) getParameter(PARAMETER_FOLD_NUMBER))));
		}
		if (!((boolean) getParameter(PARAMETER_SUBTREE_RAISING))) {
			tmpOptions.add("-S");
		}
		if (!((boolean) getParameter(PARAMETER_CLEAN_UP))) {
			tmpOptions.add("-L");
		}
		if (((boolean) getParameter(PARAMETER_LAPLACE_SMOOTHING))) {
			tmpOptions.add("-A");
		}
		if (!((boolean) getParameter(PARAMETER_MDL_CORRECTION))) {
			tmpOptions.add("-J");
		}
		// default value in J48 is 1
		if (((int) getParameter(PARAMETER_SEED)) != 1) {
			tmpOptions.add("-Q");
			tmpOptions.add(String.valueOf(((int) getParameter(PARAMETER_SEED))));
		}
		String[] options = new String[tmpOptions.size()];
		return tmpOptions.toArray(options);
	}

	/**
	 * calls {@link TreeParser#parseTreePrefix(String)} to parse a
	 * {@link LinkSpecification} from a {@link J48} tree
	 * 
	 * @param tree
	 * @return
	 */
	private LinkSpecification treeToLinkSpec(J48 tree) {
		LinkSpecification ls = new LinkSpecification();
		try {
			logger.debug(tree.prefix());
			logger.debug(tree.graph());
			String treeString = tree.prefix().substring(1,
					ParenthesisMatcher.findMatchingParenthesis(tree.prefix(), 0));
			ls = tp.pruneLS(tp.parseTreePrefix(treeString), (int) getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls;
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
		this.fmeasure = new FMeasure();
		this.negativeExample = false;
		this.positiveExample = false;
		this.trainingSet = null;
		this.bestLS = null;
		this.bestFMeasure = 0.0;
		this.prediction = null;
		alreadySeenLS = new HashMap<String, Double>();
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

	private LinkSpecification raiseThreshold(LinkSpecification ls) {
		LinkSpecification lsClone = ls.clone();
		if (lsClone.getMeasure().startsWith("MINUS")) {
			return manipulateThreshold(lsClone, true, thresholdRaise);
		}
		lsClone = manipulateThreshold(lsClone, false, thresholdRaise);
		if (lsClone.equals(ls)) {
			logger.info("Can't raise threshold anymore, maximum reached!");
		}
		return lsClone;
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
        learningParameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class, 0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
        learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,Double.class, 0d, 1d, 0.01d, PARAMETER_PROPERTY_LEARNING_RATE));
	}

	@Override
	protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
		root = new UnsupervisedDecisionTree(this, sourceCache, targetCache, pfm,(double)getParameter(PARAMETER_MIN_PROPERTY_COVERAGE), (double)getParameter(PARAMETER_PROPERTY_LEARNING_RATE));
		UnsupervisedDecisionTree.maxDepth = (int)getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT);
		root.buildTree((int)getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT));
		System.out.println(root.toString());
		root.prune();
		System.out.println(root.toString());
		LinkSpecification ls = tp.parseTreePrefix(root.toString());
		MLResults res = new MLResults(ls, UnsupervisedDecisionTree.getTotalMapping(root), -1.0, null);
		return res;
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		return mlType == MLImplementationType.SUPERVISED_ACTIVE || mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
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
			logger.info("All Candidates have been presented. Returning most certain candidates");
			base.sort((s1, s2) -> s1.compoundMeasureValue.compareTo(s2.compoundMeasureValue));
			size = (base.size() < size) ? base.size() : size;
			for (int i = 0; i < size; i++) {
				SourceTargetValue candidate = base.get(i);
				mostInformativeLinkCandidates.add(candidate.sourceUri, candidate.targetUri, candidate.value);
			}

		}
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
		return activeLearn(trainingData);
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

	public static void main(String[] args) {
		/**
		 * check this tree with checkiftherewasbetterlsbefore
		 * [qgrams§title|title: <= 0.504587, > 0.504587[jaro§authors|authors: <=
		 * 0.675724, > 0.675724[negative (304.0)][jaro§title|title: <= 0.677374,
		 * > 0.677374[qgrams§authors|authors: <= 0.355556, > 0.355556[positive
		 * (4.0)][cosine§authors|authors: <= 0.612372, > 0.612372[negative
		 * (4.0)][positive (2.0)]]][negative (7.0)]]][positive (19.0/1.0)]]
		 */
		EvaluationData c = DataSetChooser.getData("dblpscholar");
		try {
			AMLAlgorithm dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
					MLImplementationType.SUPERVISED_BATCH);
			dtl.init(null, c.getSourceCache(), c.getTargetCache());
			dtl.getMl().setConfiguration(c.getConfigReader().read());
			((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
			LinkSpecification ls = (((DecisionTreeLearning) dtl.getMl()).tp.parseTreePrefix(
					"qgrams§title|title: <= 0.504587, > 0.504587[jaro§authors|authors: <= 0.675724, > 0.675724[negative (304.0)][jaro§title|title: <= 0.677374, > 0.677374[qgrams§authors|authors: <= 0.355556, > 0.355556[positive (4.0)][cosine§authors|authors: <= 0.612372, > 0.612372[negative (4.0)][positive (2.0)]]][negative (7.0)]]][positive (19.0/1.0)]"));
			((DecisionTreeLearning) dtl.getMl()).checkIfThereWasBetterLSBefore(((DecisionTreeLearning) dtl.getMl()).tp
					.pruneLS(ls, DecisionTreeLearning.maxLinkSpecHeight));
			// CSVMappingReader reader = new
			// CSVMappingReader("/home/ohdorno/Documents/Uni/BA_Informatik/example.csv",",");
			// AMapping trainingMapping = reader.read();
			// MLResults res = dtl.asSupervised().learn(trainingMapping);
			// System.out.println(res.getLinkSpecification());
			// ((DecisionTreeLearning)dtl.getMl()).checkIfThereWasBetterLSBefore(new
			// LinkSpecification("AND(cosine(x.http://www.okkam.org/ontology_person1.owl#surname,
			// y.http://www.okkam.org/ontology_person2.owl#given_name)|0.1,
			// cosine(x.http://www.okkam.org/ontology_person1.owl#surname,
			// y.http://www.okkam.org/ontology_person2.owl#given_name)|
			// 0.1)|0.1",0.0));
		} catch (UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// DecisionTreeLearning dtl = new DecisionTreeLearning();
		// LinkSpecification resLS = new LinkSpecification(
		// "OR(jaccard(x.surname,y.name)|0.5941,OR(XOR(OR(XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728,XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728)|0.5807,OR(XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728,trigrams(x.surname,y.name)|0.5919)|0.5807)|0.7728,trigrams(x.name,y.name)|0.9728)|0.9807)",
		// 0.8);
		// System.out.println(ls);
		// System.out.println("\n\n\n");
		// System.out.println(dtl.subtractDeltaFromLS(new
		// LinkSpecification("jaccard(x.surname,y.surname)",0.1)));

	}

}
