package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatComplete;
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
    static final int expRuns = 5;
    static int initsize=0;
    private boolean ignoreZeros = false;

    static Logger logger = Logger.getLogger(DecisionTreeLearning.class);
    private PropertyMapping propertyMapping;
    private Instances trainingSet;
    private MLResults mlresult;
    private LinkSpecification deltaLS;
    private TreeParser tp;
    private J48 tree;
    private Configuration configuration;
    private HashSet<SourceTargetValue> previouslyPresentedCandidates;
    private HashSet<SourceTargetValue> base;
    /**
     * true if training data contains positive and negative examples
     */
    private boolean diverseTrainingData = true;
    /**
     * helper boolean for uniformTrainingData
     */
    private boolean negativeExample = false;
    /**
     * helper boolean for uniformTrainingData
     */
    private boolean positiveExample = false;
    /**
     * Since the most informative links are the ones near the boundary, where the instance pairs are being classified as links or not, we need to shift the
     * threshold of the measure so we can also get the instance pairs that are almost classified as links
     */
    private static final double delta = 0.1;

    // Parameters
    public static final String PARAMETER_TRAINING_DATA_SIZE = "training data size";
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

    // Default parameters
    private int trainingDataSize = 10;
    private boolean unprunedTree = false;
    private boolean collapseTree = true;
    private double pruningConfidence = 0.25;
    private boolean reducedErrorPruning = false;
    private int foldNumber = 3;
    private boolean subtreeRaising = true;
    private boolean cleanUp = true;
    private boolean laplaceSmoothing = false;
    private boolean mdlCorrection = true;
    private int seed = 1;
    private AMapping initialMapping = MappingFactory.createDefaultMapping();
    private LinkSpecification defaultLS;

    // TODO check whats wrong with these
    public static final String[] stringMeasures = { "cosine",
	    // "exactmatch",
	    "jaccard", "jaro",
	    // "levenshtein",
	    "qgrams", "trigrams" };
    public static final String[] dateMeasures = { "datesim", "daysim", "yearsim" };
    public static final String[] pointsetMeasures = { "symmetrichausdorff", "frechet", "hausdorff", "geolink", "geomean", "geolink", "surjection",
	    "fairsurjection" };
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
	} else if (this.defaultLS != null) {
	    logger.info("...by running given LinkSpecification...");
	    DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
	    SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		    .getTargetInfo().getVar());
	    mapping = ee.execute(defaultLS, dp);
	} else if (this.configuration != null) {
	    logger.info("...by running LinkSpecification from Configuration...");
	    if (this.configuration.getMetricExpression() != null && !this.configuration.getMetricExpression().isEmpty()) {
		defaultLS = new LinkSpecification();
		defaultLS.readSpec(this.configuration.getMetricExpression(), this.configuration.getAcceptanceThreshold());
		DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
			.getTargetInfo().getVar());
		mapping = ee.execute(defaultLS, dp);
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
	Instances trainingInstances = new Instances("Rel", attributes, mapping.size());
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
	    return sourceUri + " -> " + targetUri + " : " + value + "     | compound measure value: " + compoundMeasureValue;
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
	mapping.getMap().forEach(
		(sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			Instance inst = new DenseInstance(trainingSet.numAttributes());
			inst.setDataset(trainingSet);
			for (int i = 0; i < trainingSet.numAttributes(); i++) {
			    if (i != trainingSet.classIndex()) {
				String[] measureAndProperties = tp.getMeasureAndProperties(trainingSet.attribute(i).name());
				String measureName = measureAndProperties[0];
				String propertyA = measureAndProperties[1];
				String propertyB = measureAndProperties[2];
				String metricExpression = measureName + "(x." + propertyA + ", y." + propertyB + ")";
				if(targetCache.getInstance(targetURI) == null || sourceCache.getInstance(sourceURI) == null){
				    if (sourceCache.getInstance("<" + sourceURI + ">") == null || targetCache.getInstance("<" + targetURI + ">") == null) {
                                        logger.error("URI from training mapping cannot be found in source/target cache.\n sourceURI: " + sourceURI + " targetURI: " + targetURI + " : " + value);
				    }else{
					if (!ignoreZeros) {
					    inst.setValue(
						    i,
						    MeasureProcessor.getSimilarity(sourceCache.getInstance("<" + sourceURI + ">"),
							    targetCache.getInstance("<" + targetURI + ">"), metricExpression, threshold, "?x", "?y"));
					} else {
					    double simValue = MeasureProcessor.getSimilarity(sourceCache.getInstance("<" + sourceURI + ">"),
						    targetCache.getInstance("<" + targetURI + ">"), metricExpression, threshold, "?x", "?y");
					    if (simValue > 0) {
						inst.setValue(i, simValue);
					    }
					}
				    }
				}else{
				    if (!ignoreZeros) {
					inst.setValue(i, MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI), targetCache.getInstance(targetURI),
						metricExpression, threshold, "?x", "?y"));
				    } else {
					double simValue = MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI),
						targetCache.getInstance(targetURI), metricExpression, threshold, "?x", "?y");
					if (simValue > 0) {
					    inst.setValue(i, simValue);
					}
				    }
				}
			    } else {
				String classValue = "negative";
				if (value == 1.0) {
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
	AMapping trainingData = getTrainingMapping();
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
	System.out.println("Mapping size" + oracleMapping.size());
	if(oracleMapping.size() == 0){
	    logger.error("empty oracle Mapping! Returning empty MLResults!");
	    return new MLResults();
	}
//	if(base.size() != 0){
//	    Iterator<SourceTargetValue> it = base.iterator();
//	    while(it.hasNext()){
//		SourceTargetValue inst = it.next();
//		oracleMapping.add(inst.sourceUri, inst.targetUri, inst.value);
//	    }
//	}
//	int lefToAdd = oracleMapping.size();
	int positive = 0;
	int negative = 0;
        for(String s : oracleMapping.getMap().keySet()){
            for(String t : oracleMapping.getMap().get(s).keySet()){
        	if(oracleMapping.getMap().get(s).get(t) == 1.0){
//        	    System.out.println(t + " -> " + s);
        	    positive++;
        	}else{
//        	    System.out.println(t + " -> " + s);
        	    negative++;
        	}
            }
        }
//        System.out.println(positive + " + " + negative + " = " + (positive + negative)  + " | " + oracleMapping.size());
	
//	//Since J48 is not updateable we add all the training data we used previously
//	if(previouslyPresentedCandidates.size() != 0){
//	    Iterator<SourceTargetValue> it = previouslyPresentedCandidates.iterator();
//	    boolean lastWasPos = false;
//	    while(lefToAdd != 0 && it.hasNext()){
//		
//            SourceTargetValue instance = it.next();
//            	
//		if (!lastWasPos && instance.value == 1.0) {
//		    if (!oracleMapping.contains(instance.sourceUri, instance.targetUri)) {
//			oracleMapping.add(instance.sourceUri, instance.targetUri, instance.value);
//			lastWasPos = true;
//		    }
//		}
//		if (lastWasPos && instance.value != 1.0) {
//		    if (!oracleMapping.contains(instance.sourceUri, instance.targetUri)) {
//			oracleMapping.add(instance.sourceUri, instance.targetUri, instance.value);
//			lastWasPos = false;
//		    }
//		}
//		//                oracleMapping.add(instance.sourceUri, instance.targetUri, instance.value);
//	    }
//	}
	if(previouslyPresentedCandidates.size() != 0){
	    Iterator<SourceTargetValue> it = previouslyPresentedCandidates.iterator();
            while((positive != negative) && it.hasNext()){ 
            SourceTargetValue instance = it.next();
            if((positive < negative) && instance.value == 1.0){
        	if(!oracleMapping.contains(instance.sourceUri, instance.targetUri)){
                oracleMapping.add(instance.sourceUri, instance.targetUri, instance.value);
                positive++;
        	}
            }
            if((positive > negative) && instance.value != 1.0){
        	if(!oracleMapping.contains(instance.sourceUri, instance.targetUri)){
                oracleMapping.add(instance.sourceUri, instance.targetUri, instance.value);
                negative++;
        	}
            }

            }
	}
	// These are the instances labeled by the user so we keep them to not
	// present the same pairs twice
	oracleMapping.getMap().forEach((sourceURI, map2) -> {
	    map2.forEach((targetURI, value) -> {
		previouslyPresentedCandidates.add(new SourceTargetValue(sourceURI, targetURI, value));
	    });
	});
	this.trainingSet = createEmptyTrainingInstances(oracleMapping);
	fillInstances(trainingSet, oracleMapping);
	if (!(negativeExample & positiveExample)) {
	    logger.info("negative examples: " + negativeExample + " positive examples:" + positiveExample);
            negativeExample = false;
            positiveExample = false;
	    return handleUniformTrainingData(oracleMapping);
	}
	String[] options = getOptionsArray();
	tree = new J48();
	try {
	    tree.setOptions(options);
	    logger.info("Building classifier....");
	    tree.buildClassifier(trainingSet);
	    logger.info("Parsing tree to LinkSpecification...");
            System.err.println(tree.prefix());
            System.err.println(tree.graph());
            LinkSpecification resLS = treeToLinkSpec(tree);
            this.mlresult = new MLResults();
            logger.info("Learned LinkSpecification: " + resLS.toStringOneLine());
            deltaLS = subtractDeltaFromLS(resLS);
            this.mlresult.setLinkSpecification(resLS);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return this.mlresult;
    }


    /**
     * calls wombat because it is designed to handle this case    
     * @param oracleMapping mapping containing user labeled data
     * @return mlResult containing the result
     * @throws UnsupportedMLImplementationException 
     */
    public MLResults handleUniformTrainingData(AMapping oracleMapping) throws UnsupportedMLImplementationException {
	logger.info("Training Data contains only positive/negative examples. Using Wombat");
	ActiveMLAlgorithm wombatSimpleA = null;
	try {
	    wombatSimpleA = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class, MLImplementationType.SUPERVISED_ACTIVE).asActive();
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
	if (unprunedTree) {
	    tmpOptions.add("-U");
	}
	if (!collapseTree) {
	    tmpOptions.add("-O");
	}
	// default value in J48 is 0.25
	if (pruningConfidence != 0.25) {
	    tmpOptions.add("-C");
	    tmpOptions.add(String.valueOf(pruningConfidence));
	}
	if (reducedErrorPruning) {
	    tmpOptions.add("-R");
	}
	// default value in J48 is 3
	if (foldNumber != 3) {
	    tmpOptions.add("-N");
	    tmpOptions.add(String.valueOf(foldNumber));
	}
	if (!subtreeRaising) {
	    tmpOptions.add("-S");
	}
	if (!cleanUp) {
	    tmpOptions.add("-L");
	}
	if (laplaceSmoothing) {
	    tmpOptions.add("-A");
	}
	if (!mdlCorrection) {
	    tmpOptions.add("-J");
	}
	// default value in J48 is 1
	if (seed != 1) {
	    tmpOptions.add("-Q");
	    tmpOptions.add(String.valueOf(seed));
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
	    String treeString = tree.prefix().substring(1, ParenthesisMatcher.findMatchingParenthesis(tree.prefix(), 0));
	    TreeParser.measuresUsed.clear();
	    ls = tp.parseTreePrefix(treeString);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return ls;
    }

    @Override
    public AMapping predict(ACache source, ACache target, MLResults mlModel) {
	LinkSpecification ls = mlresult.getLinkSpecification();
	DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		.getTargetInfo().getVar());
	return ee.execute(ls, dp);
    }

    @Override
    public void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
	super.init(lp, sourceCache, targetCache);
	previouslyPresentedCandidates = new HashSet<SourceTargetValue>();
	base = new HashSet<SourceTargetValue>();
	if (lp == null) {
	    setDefaultParameters();
	} else {
	    setLearningParameters(lp);
	}
    }

    private void setLearningParameters(List<LearningParameter> lp) {
	for (LearningParameter l : lp) {
	    switch (l.getName()) {
	    case PARAMETER_TRAINING_DATA_SIZE:
		this.trainingDataSize = (Integer) l.getValue();
		break;
	    case PARAMETER_UNPRUNED_TREE:
		this.unprunedTree = (Boolean) l.getValue();
		break;
	    case PARAMETER_COLLAPSE_TREE:
		this.collapseTree = (Boolean) l.getValue();
		break;
	    case PARAMETER_PRUNING_CONFIDENCE:
		this.pruningConfidence = (Double) l.getValue();
		break;
	    case PARAMETER_REDUCED_ERROR_PRUNING:
		this.reducedErrorPruning = (Boolean) l.getValue();
		break;
	    case PARAMETER_FOLD_NUMBER:
		this.foldNumber = (Integer) l.getValue();
		break;
	    case PARAMETER_SUBTREE_RAISING:
		this.subtreeRaising = (Boolean) l.getValue();
		break;
	    case PARAMETER_CLEAN_UP:
		this.cleanUp = (Boolean) l.getValue();
		break;
	    case PARAMETER_LAPLACE_SMOOTHING:
		this.laplaceSmoothing = (Boolean) l.getValue();
		break;
	    case PARAMETER_MDL_CORRECTION:
		this.mdlCorrection = (Boolean) l.getValue();
		break;
	    case PARAMETER_SEED:
		this.seed = (Integer) l.getValue();
		break;
	    case PARAMETER_PROPERTY_MAPPING:
		this.propertyMapping = (PropertyMapping) l.getValue();
		break;
	    case PARAMETER_MAPPING:
		this.initialMapping = (AMapping) l.getValue();
		break;
	    case PARAMETER_LINK_SPECIFICATION:
		this.defaultLS = (LinkSpecification) l.getValue();
		break;
	    default:
		logger.error("Unknown parameter name. Setting defaults!");
		setDefaultParameters();
		break;
	    }
	}
    }

    /**
     * Adds the delta shifting of the thresholds in the measures
     * 
     * @param ls
     *            LinkSpec to be cleaned of the delta shift
     * @return cleaned LinkSpec
     */
    private LinkSpecification subtractDeltaFromLS(LinkSpecification ls) {
	LinkSpecification lsClone = ls.clone();
	if (lsClone.getMeasure().startsWith("MINUS")) {
	    return subtractDeltaFromLS(lsClone, true);
	}
	return subtractDeltaFromLS(lsClone, false);
    }

    /**
     * Adds the delta shifting of the thresholds in the measures
     * 
     * @param ls
     *            LinkSpec to be cleaned of the delta shift
     * @param parentIsMinus
     *            true if measure is minus
     * @return cleaned LinkSpec
     */
    private LinkSpecification subtractDeltaFromLS(LinkSpecification ls, boolean measureIsMinus) {
	if (ls.isAtomic()) {
	    if (!measureIsMinus) {
		    // have to use BigDecimal because of floating numbers magic
		    ls.setThreshold(Math.max(0.01,(BigDecimal.valueOf(ls.getThreshold()).subtract(BigDecimal.valueOf(delta))).doubleValue()));
		return ls;
	    } else {
		if(ls.getThreshold() == 0.0){
		    return ls;
		}
		ls.setThreshold(Math.min(1.0,(BigDecimal.valueOf(ls.getThreshold()).add(BigDecimal.valueOf(delta))).doubleValue()));
		return ls;
	    }
	}
	ArrayList<LinkSpecification> newChildren = new ArrayList<LinkSpecification>();
	if (ls.getMeasure().startsWith("MINUS")) {
	    for (LinkSpecification l : ls.getChildren()) {
		newChildren.add(subtractDeltaFromLS(l, true));
	    }
	    ls.setChildren(newChildren);
	} else {
	    for (LinkSpecification l : ls.getChildren()) {
		newChildren.add(subtractDeltaFromLS(l, false));
	    }
	    ls.setChildren(newChildren);
	}
	return ls;
    }

    public LinkSpecification getDefaultLS() {
	return defaultLS;
    }

    @Override
    public void setDefaultParameters() {
	parameters = new ArrayList<>();
	parameters.add(new LearningParameter(PARAMETER_TRAINING_DATA_SIZE, trainingDataSize, Integer.class, 1, 100000, 1, PARAMETER_TRAINING_DATA_SIZE));
	parameters.add(new LearningParameter(PARAMETER_UNPRUNED_TREE, unprunedTree, Boolean.class, 0, 1, 0, PARAMETER_UNPRUNED_TREE));
	parameters.add(new LearningParameter(PARAMETER_COLLAPSE_TREE, collapseTree, Boolean.class, 0, 1, 1, PARAMETER_COLLAPSE_TREE));
	parameters.add(new LearningParameter(PARAMETER_PRUNING_CONFIDENCE, pruningConfidence, Double.class, 0d, 1d, 0.01d, PARAMETER_PRUNING_CONFIDENCE));
	parameters.add(new LearningParameter(PARAMETER_REDUCED_ERROR_PRUNING, reducedErrorPruning, Boolean.class, 0, 1, 0, PARAMETER_REDUCED_ERROR_PRUNING));
	parameters.add(new LearningParameter(PARAMETER_FOLD_NUMBER, foldNumber, Integer.class, 0, 10, 1, PARAMETER_FOLD_NUMBER));
	parameters.add(new LearningParameter(PARAMETER_SUBTREE_RAISING, subtreeRaising, Boolean.class, 0, 1, 0, PARAMETER_SUBTREE_RAISING));
	parameters.add(new LearningParameter(PARAMETER_CLEAN_UP, cleanUp, Boolean.class, 0, 1, 0, PARAMETER_CLEAN_UP));
	parameters.add(new LearningParameter(PARAMETER_LAPLACE_SMOOTHING, laplaceSmoothing, Boolean.class, 0, 1, 0, PARAMETER_LAPLACE_SMOOTHING));
	parameters.add(new LearningParameter(PARAMETER_MDL_CORRECTION, mdlCorrection, Boolean.class, 0, 1, 0, PARAMETER_MDL_CORRECTION));
	parameters.add(new LearningParameter(PARAMETER_SEED, seed, Integer.class, 0, 100, 1, PARAMETER_SEED));
	parameters.add(new LearningParameter(PARAMETER_PROPERTY_MAPPING, propertyMapping, PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN,
		PARAMETER_PROPERTY_MAPPING));
	parameters.add(new LearningParameter(PARAMETER_MAPPING, initialMapping, AMapping.class, Double.NaN, Double.NaN, Double.NaN, PARAMETER_MAPPING));
	parameters.add(new LearningParameter(PARAMETER_LINK_SPECIFICATION, defaultLS, LinkSpecification.class, Double.NaN, Double.NaN, Double.NaN,
		PARAMETER_LINK_SPECIFICATION));
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
	throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
	return mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }

    /**
     * Executes the {@link #deltaLS} and calculates the compound measure for
     * each instance pair in the resulting mapping compound measure is sum of
     * |measure(s,t) - threshold of used measure|^2
     */
    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
	logger.info("Getting next examples to present user...");
	if(size == 0){
	    logger.error("next example size is 0! Returning empty mapping!);");
	    return MappingFactory.createDefaultMapping();
	}
	DynamicPlanner dp = new DynamicPlanner(this.sourceCache, this.targetCache);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(this.sourceCache, this.targetCache, this.configuration.getSourceInfo().getVar(),
		this.configuration.getTargetInfo().getVar());
	AMapping deltaMapping = ee.execute(deltaLS, dp);
	ArrayList<SourceTargetValue> tmpCandidateList = new ArrayList<SourceTargetValue>();
	deltaMapping.getMap().forEach(
		(sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			double compoundMeasureValue = 0;
			for (Map.Entry<String, Double> entry : TreeParser.measuresUsed.entrySet()) {
			    compoundMeasureValue += Math.pow(
				    Math.abs(MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI), targetCache.getInstance(targetURI),
					    entry.getKey(), threshold, "?x", "?y")
					    - entry.getValue()), 2);
			}
			SourceTargetValue candidate = new SourceTargetValue(sourceURI, targetURI, value, compoundMeasureValue);
			if (!previouslyPresentedCandidates.contains(candidate)) {
			    tmpCandidateList.add(candidate);
			}
		    });
		});
	tmpCandidateList.sort((s1, s2) -> s1.compoundMeasureValue.compareTo(s2.compoundMeasureValue));
//	for(int i = 0; i < 200; i++){
//	    System.out.println(tmpCandidateList.get(i));
//	}

	AMapping mostInformativeLinkCandidates = MappingFactory.createDefaultMapping();

	size = (tmpCandidateList.size() < size) ? tmpCandidateList.size() : size;
	for (int i = 0; i < size; i++) {
	    SourceTargetValue candidate = tmpCandidateList.get(i);
	    mostInformativeLinkCandidates.add(candidate.sourceUri, candidate.targetUri, candidate.value);
	}
	for(int i = 1; i < size +1; i++){
	    SourceTargetValue candidate = tmpCandidateList.get(tmpCandidateList.size() - i);
	    candidate.value = 1.0;
	    base.add(candidate);
	}
	return mostInformativeLinkCandidates;
    }

    @Override
    protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
	throw new UnsupportedMLImplementationException(this.getName());
    }

    public PropertyMapping getPropertyMapping() {
	return propertyMapping;
    }

    public void setPropertyMapping(PropertyMapping propertyMapping) {
	this.propertyMapping = propertyMapping;
    }

    public Configuration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(Configuration configuration) {
	this.configuration = configuration;
    }

    public MLResults getMlresult() {
	return mlresult;
    }

    public static void main(String[] args) {
	QualitativeMeasuresEvaluator eval = new QualitativeMeasuresEvaluator();
//    final String[] datasetsList = {DataSetChooser.DataSets.DRUGS.toString()};
	final String[] datasetsList = {/*DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString(),*/ DataSetChooser.DataSets.DBPLINKEDMDB.toString(),DataSetChooser.DataSets.PERSON1.toString(), DataSetChooser.DataSets.PERSON2.toString(), DataSetChooser.DataSets.DBLPACM.toString(), DataSetChooser.DataSets.DRUGS.toString()};
	String[][] resultsMatrix = new String[expRuns + 1][datasetsList.length];
        Set<TaskData> tasks =new TreeSet<TaskData>();
        TaskData task = new TaskData();
        EvaluationData c = null;
        int dscount = 0;
        try {
            for (String ds : datasetsList) {
                logger.info(ds);
                c = DataSetChooser.getData(ds);
                GoldStandard gs = new GoldStandard(c.getReferenceMapping(),c.getSourceCache(),c.getTargetCache());
                //extract training data

                AMapping reference =  c.getReferenceMapping();
                
                AMapping training = extractTrainingData(reference, 0.9);
                task = new TaskData(gs, c.getSourceCache(), c.getTargetCache(), c);
                task.initialMapping = extractTrainingData(reference, -1.0);
                task.dataName = ds;
                task.training = training;
                tasks.add(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }        Set<EvaluatorType> evaluators=null;
        try {
            evaluators=new TreeSet<EvaluatorType>();
//            evaluators.add(EvaluatorType.PRECISION);
//            evaluators.add(EvaluatorType.RECALL);
            evaluators.add(EvaluatorType.F_MEASURE);
//            evaluators.add(EvaluatorType.P_PRECISION);
//            evaluators.add(EvaluatorType.P_RECALL);
            evaluators.add(EvaluatorType.PF_MEASURE);
//            evaluators.add(EvaluatorType.ACCURACY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(TaskData td : tasks){
        DecisionTreeLearning dtl = new DecisionTreeLearning(td.evalData.getConfigReader().getConfiguration());
        dtl.setPropertyMapping(td.evalData.getPropertyMapping());
        dtl.init(null, td.evalData.getSourceCache(), td.evalData.getTargetCache());
        dtl.setInitialMapping(td.initialMapping);
//        AMapping initialMapping = MappingFactory.createDefaultMapping();
//        initsize = (int)Math.round(0.5*td.training.size());
//        td.training.getMap().forEach(
//            (sourceURI, map2) -> {
//                map2.forEach((targetURI, value) -> {
//                    if(initsize >= 0){
//                        initialMapping.add(sourceURI, targetURI, value);
//                        initsize--;
//                    }
//                });
//            });
//        dtl.setInitialMapping(initialMapping);
        try {
            resultsMatrix[0][dscount] = td.dataName;
	    MLResults res = dtl.activeLearn();
            AMapping predictions = dtl.predict(td.source, td.target, res);
        Map<EvaluatorType, Double> evaluationResults = eval.evaluate(predictions, td.goldStandard, evaluators);
            EvaluationRun er = new EvaluationRun(dtl.getName().replaceAll("\\s+", ""),"SUPERVISED_ACTIVE".replaceAll("//s", ""),td.dataName.replaceAll("//s", ""),evaluationResults);
        	    if(er.qualititativeScores.get(EvaluatorType.F_MEASURE)!=0.0){
        	    resultsMatrix[1][dscount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.F_MEASURE));
        	    }else{
        	    resultsMatrix[1][dscount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.PF_MEASURE));
        	    }
            er.display();
            for(int i = 2 ; i <= expRuns; i++){
                AMapping nextExamples = dtl.getNextExamples((int)Math.round((double)expRuns * 0.1 *td.training.size()));
                AMapping oracleFeedback = oracleFeedback(nextExamples, td.training);
//                AMapping oracleFeedback = oracleFeedback(nextExamples, td.evalData.getReferenceMapping());
                res = dtl.activeLearn(oracleFeedback);
                predictions = dtl.predict(td.source, td.target, res);
                evaluationResults = eval.evaluate(predictions, td.goldStandard, evaluators);
                er = new EvaluationRun(dtl.getName().replaceAll("\\s+", ""),"SUPERVISED_ACTIVE".replaceAll("//s", ""),td.dataName.replaceAll("//s", ""),evaluationResults);
        	    if(er.qualititativeScores.get(EvaluatorType.F_MEASURE)!=0.0){
                        resultsMatrix[i][dscount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.F_MEASURE));
        	    }else{
                        resultsMatrix[i][dscount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.PF_MEASURE));
        	    }
                er.display();
            }

	} catch (UnsupportedMLImplementationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
            }
            dscount++;
        }
	for(int i = 0; i <= expRuns; i++){
        	System.out.println();
        	for(int j = 0; j < datasetsList.length; j++){
        	    System.out.print("\t" + resultsMatrix[i][j]);
        	}
        }

	
    } 
    private static AMapping oracleFeedback(AMapping predictionMapping, AMapping referenceMapping) {
        AMapping result = MappingFactory.createDefaultMapping();

        for(String s : predictionMapping.getMap().keySet()){
            for(String t : predictionMapping.getMap().get(s).keySet()){
                if(referenceMapping.contains(s, t) && referenceMapping.getConfidence(s, t) == 1.0){
//                    result.add(s, t, predictionMapping.getMap().get(s).get(t));
                    result.add(s, t, 1.0);
                }else{
                    result.add(s, t, 0.0);
                }
            }
        }
        return result;
    }
	
	public static void testAgainstOthers(){
	DecisionTreeLearning.ExperimentResult expRes = new DecisionTreeLearning.ExperimentResult();
	for(int count = 0; count < expRuns; count++){
	    System.err.println("\n\n ================= " + count + " =============== \n\n");
//=========================== DATASETS INITIALIZATION ===============================

//    final String[] datasetsList = {DataSetChooser.DataSets.DRUGS.toString()};
    final String[] datasetsList = {/*DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString(), */DataSetChooser.DataSets.DBPLINKEDMDB.toString(),DataSetChooser.DataSets.PERSON1.toString(), DataSetChooser.DataSets.PERSON2.toString(), DataSetChooser.DataSets.DBLPACM.toString(), DataSetChooser.DataSets.DRUGS.toString()};
        Set<TaskData> tasks =new TreeSet<TaskData>();
        TaskData task = new TaskData();
        EvaluationData c = null;
        try {
            for (String ds : datasetsList) {
                logger.info(ds);
                c = DataSetChooser.getData(ds);
                GoldStandard gs = new GoldStandard(c.getReferenceMapping(),c.getSourceCache(),c.getTargetCache());
                //extract training data

                AMapping reference =  c.getReferenceMapping();
                
                AMapping training = extractTrainingData(reference, 0.9);
                task = new TaskData(gs, c.getSourceCache(), c.getTargetCache(), c);
                task.initialMapping = extractTrainingData(reference, -1.0);
                task.dataName = ds;
                task.training = training;
                tasks.add(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
//        System.out.println(c.getReferenceMapping());
//        for(org.aksw.limes.core.io.cache.Instance i : c.getSourceCache().getAllInstances()){
//            System.out.println(i.getUri());
//        }
//        System.exit(1);
//========================== EVALUATORS INITIALIZATION =============================
        Set<EvaluatorType> evaluators=null;
        try {
            evaluators=new TreeSet<EvaluatorType>();
//            evaluators.add(EvaluatorType.PRECISION);
//            evaluators.add(EvaluatorType.RECALL);
            evaluators.add(EvaluatorType.F_MEASURE);
//            evaluators.add(EvaluatorType.P_PRECISION);
//            evaluators.add(EvaluatorType.P_RECALL);
            evaluators.add(EvaluatorType.PF_MEASURE);
//            evaluators.add(EvaluatorType.ACCURACY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//========================= ALGORITHMS INITIALIZATION ============================
        
        List<TaskAlgorithm> algorithms = null;
    final String[] algorithmsList = {"SUPERVISED_ACTIVE:DECISIONTREELEARNING" , "SUPERVISED_ACTIVE:WOMBATSIMPLE","UNSUPERVISED:WOMBATSIMPLE"};
        try
        {
            algorithms = new ArrayList<TaskAlgorithm>();
            for (String algorithmItem : algorithmsList) {
                String[] algorithmTitles = algorithmItem.split(":");// split to get the type and the name of the algorithm
                MLImplementationType algType = MLImplementationType.valueOf(algorithmTitles[0]);// get the type of the algorithm

                List<LearningParameter> mlParameter = null;
                AMLAlgorithm mlAlgorithm = null;
                //check the mlImplementation Type
                if(algType.equals(MLImplementationType.SUPERVISED_ACTIVE))
                {
                    if(algorithmTitles[1].equals("EAGLE"))//create its core as eagle - it will be enclosed inside SupervisedMLAlgorithm that extends AMLAlgorithm
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asActive(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asActive(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asActive(); //create an wombat complete learning algorithm
                    else if(algorithmTitles[1].equals("DECISIONTREELEARNING")){
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,algType).asActive(); 
                    }
                    
                   mlParameter = initializeLearningParameters(algorithmTitles[1]);

                }
                else if(algType.equals(MLImplementationType.SUPERVISED_BATCH))
                {
                    if(algorithmTitles[1].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asSupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asSupervised(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asSupervised(); //create an wombat complete learning algorithm
                    
                    mlParameter = initializeLearningParameters(algorithmTitles[1]);

                }
                else if(algType.equals(MLImplementationType.UNSUPERVISED))
                {
                    if(algorithmTitles[1].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asUnsupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asUnsupervised(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asUnsupervised(); //create an wombat complete learning algorithm
                    
                    mlParameter = initializeLearningParameters(algorithmTitles[1]);

                }
                algorithms.add(new TaskAlgorithm(algType, mlAlgorithm, mlParameter));// add to list of algorithms
            }

        }catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
        }
            Evaluator evaluator = new Evaluator();
            List<EvaluationRun> results = evaluator.evaluate(algorithms, tasks, evaluators, null);
            for (EvaluationRun er : results) {
                er.display();
            }
            String[][] resMatrix = new String[4][results.size()];
            int dtlcount = 0;
            int wsupcount = 0;
            int wunsupcount = 0;
            for (EvaluationRun er : results) {
        	if(er.getAlgorithmName().startsWith("Decision")){
        	    resMatrix[0][dtlcount] = er.getDatasetName();
        	    if(er.qualititativeScores.get(EvaluatorType.F_MEASURE)!=0.0){
        	    resMatrix[1][dtlcount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.F_MEASURE));
        	    }else{
        	    resMatrix[1][dtlcount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.PF_MEASURE));
        	    }
        	    dtlcount++;
        	}
        	if(er.getAlgorithmName().startsWith("Wombat") && er.getImplementationType().startsWith("SUPERVISED")){
        	    if(er.qualititativeScores.get(EvaluatorType.F_MEASURE)!=0.0){
        	    resMatrix[2][wsupcount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.F_MEASURE));
        	    }else{
        	    resMatrix[2][wsupcount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.PF_MEASURE));
        	    }
        	    wsupcount++;
        	}
        	if(er.getAlgorithmName().startsWith("Wombat") && er.getImplementationType().startsWith("UNSUP")){
        	    if(er.qualititativeScores.get(EvaluatorType.F_MEASURE)!=0.0){
        	    resMatrix[3][wunsupcount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.F_MEASURE));
        	    }else{
        	    resMatrix[3][wunsupcount] = String.valueOf(er.qualititativeScores.get(EvaluatorType.PF_MEASURE));
        	    }
        	    wunsupcount++;
        	}
                System.out.println(er);
            }
            
//            System.out.println("\n DTL \t WSUP \t WUNSUP");
//            PrintWriter writer;
//	    try {
//		writer = new PrintWriter("/home/ohdorno/Documents/Uni/BA_Informatik/Experiments/17_08TrainingOracle/" + count + "ResultsDTL_WSUP_WUNSUP.dat", "UTF-8");
//		writer.println("\n DTL \t WSUP \t WUNSUP");
            for(int i = 0; i < 5; i++){
        	System.out.println(resMatrix[0][i] + "\t" + resMatrix[1][i] + "\t" + resMatrix[2][i] + "\t" + resMatrix[3][i]);
//        	writer.println(resMatrix[0][i] + "\t" + resMatrix[1][i] + "\t" + resMatrix[2][i] + "\t" + resMatrix[3][i]);
        	expRes.allArrs.get(i)[count] = Double.valueOf(resMatrix[1][i]);
//        	expRes.allArrs.get(i + 5)[count] = Double.valueOf(resMatrix[2][i]);
//        	expRes.allArrs.get(i + 10)[count] = Double.valueOf(resMatrix[3][i]);
            }
//            writer.close();
//	    } catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    } catch (UnsupportedEncodingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }
	}
//	expRes.middle();
//            PrintWriter writer;
//	    try {
//		writer = new PrintWriter("/home/ohdorno/Documents/Uni/BA_Informatik/Experiments/17_08WithZeros/MIDDLEDResultsDTL_WSUP_WUNSUP.dat", "UTF-8");
//		writer.println("DecisionTreeLearning \t WombatSupervised \t WombatUnsupervised");
////		writer.println(expRes.dtlDBLPACMmiddled + "\t" + expRes.wsupDBLPACMmiddled + "\t" + expRes.wunsupDBLPACMmiddled);
////		writer.println(expRes.dtlDBPLINKEDMDBmiddled + "\t" + expRes.wsupDBPLINKEDMDBmiddled + "\t" + expRes.wunsupDBPLINKEDMDBmiddled);
////		writer.println(expRes.dtlDRUGSmiddled + "\t" + expRes.wsupDRUGSmiddled + "\t" + expRes.wunsupDRUGSmiddled);
////		writer.println(expRes.dtlPERSON1middled + "\t" + expRes.wsupPERSON1middled + "\t" + expRes.wunsupPERSON1middled);
////		writer.println(expRes.dtlPERSON2middled + "\t" + expRes.wsupPERSON2middled + "\t" + expRes.wunsupPERSON2middled);
//		writer.println(expRes.dtlDBLPACMmiddled + "\t0.002972113819037035\t0.26617082241053464");
//		writer.println(expRes.dtlDBPLINKEDMDBmiddled + "\t0.32646539399545876\t0.9429814517975726");
//		writer.println(expRes.dtlDRUGSmiddled + "\t0.9719764011799411\t0.9719764011799411");
//		writer.println(expRes.dtlPERSON1middled + "\t0.0\t0.003992015968063872");
//		writer.println(expRes.dtlPERSON2middled + "\t0.0\t0.0033277870216306157");
//            writer.close();
//	    } catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    } catch (UnsupportedEncodingException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }

    }
    
    public static class ExperimentResult{
	public ArrayList<double[]> allArrs = new ArrayList<double[]>();
	public double[] dtlDBLPACM = new double[expRuns];
	public double[] dtlDBPLINKEDMDB = new double[expRuns];
	public double[] dtlDRUGS = new double[expRuns];
	public double[] dtlPERSON1 = new double[expRuns];
	public double[] dtlPERSON2 = new double[expRuns];
	public double[] wsupDBLPACM = new double[expRuns];
	public double[] wsupDBPLINKEDMDB = new double[expRuns];
	public double[] wsupDRUGS = new double[expRuns];
	public double[] wsupPERSON1 = new double[expRuns];
	public double[] wsupPERSON2 = new double[expRuns];
	public double[] wunsupDBLPACM = new double[expRuns];
	public double[] wunsupDBPLINKEDMDB = new double[expRuns];
	public double[] wunsupDRUGS = new double[expRuns];
	public double[] wunsupPERSON1 = new double[expRuns];
	public double[] wunsupPERSON2 = new double[expRuns];
	
	private ArrayList<Double> all = new ArrayList<Double>();
	public double dtlDBLPACMmiddled;
	public double dtlDBPLINKEDMDBmiddled;
	public double dtlDRUGSmiddled;
	public double dtlPERSON1middled;
	public double dtlPERSON2middled;
	public double wsupDBLPACMmiddled;
	public double wsupDBPLINKEDMDBmiddled;
	public double wsupDRUGSmiddled;
	public double wsupPERSON1middled;
	public double wsupPERSON2middled;
	public double wunsupDBLPACMmiddled;
	public double wunsupDBPLINKEDMDBmiddled;
	public double wunsupDRUGSmiddled;
	public double wunsupPERSON1middled;
	public double wunsupPERSON2middled;
	
	public ExperimentResult(){
	    allArrs.add(dtlDBLPACM);
	    allArrs.add(dtlDBPLINKEDMDB);
            allArrs.add(dtlDRUGS);
            allArrs.add(dtlPERSON1);
            allArrs.add(dtlPERSON2);
            allArrs.add(wsupDBLPACM);
            allArrs.add(wsupDBPLINKEDMDB);
            allArrs.add(wsupDRUGS);
            allArrs.add(wsupPERSON1);
            allArrs.add(wsupPERSON2);
            allArrs.add(wunsupDBLPACM);
            allArrs.add(wunsupDBPLINKEDMDB);
            allArrs.add(wunsupDRUGS);
            allArrs.add(wunsupPERSON1);
            allArrs.add(wunsupPERSON2);
	}
	
	public void middle(){
	    for(double[] currentArr: allArrs){
		
		double middle = 0;
		for(int i = 0; i < expRuns;i++){
		    middle += currentArr[i];
		}
		all.add(middle/expRuns);
	    }
             dtlDBLPACMmiddled = all.get(0);
             dtlDBPLINKEDMDBmiddled = all.get(1);
             dtlDRUGSmiddled = all.get(2);
             dtlPERSON1middled = all.get(3);
             dtlPERSON2middled = all.get(4);
             wsupDBLPACMmiddled = all.get(5);
             wsupDBPLINKEDMDBmiddled = all.get(6);
             wsupDRUGSmiddled = all.get(7);
             wsupPERSON1middled = all.get(8);
             wsupPERSON2middled = all.get(9);
             wunsupDBLPACMmiddled = all.get(10);
             wunsupDBPLINKEDMDBmiddled = all.get(11);
             wunsupDRUGSmiddled = all.get(12);
             wunsupPERSON1middled = all.get(13);
             wunsupPERSON2middled = all.get(14);
	    
	    
	}
    }

    private static AMapping extractTrainingData(AMapping reference, double factor)
    {
        AMapping training = MappingFactory.createDefaultMapping();
        int trainingSize = 0;
        if(factor == -1.0){
            trainingSize = 10;
        }else{
        	trainingSize = (int) Math.ceil(factor*reference.getSize());
        }
        HashMap<String, HashMap<String,Double>> refMap = reference.getMap();

        Random       random    = new Random();
        List<String> keys      = new ArrayList<String>(refMap.keySet());
        List<String> values = null;
        if(reference instanceof MemoryMapping){
            values = new ArrayList<String>(((MemoryMapping)reference).reverseSourceTarget().getMap().keySet());
        }else{
            logger.error("HybridMapping not implemented");
        }
        for(int i=0 ; i< trainingSize ;i++){
            if(keys.size()> 0){
            if(i%2 != 0){
                String sourceInstance = keys.get( random.nextInt(keys.size()) );
//                String sourceInstance = keys.get(i);
                HashMap<String,Double> targetInstance = refMap.get(sourceInstance);
                keys.remove(sourceInstance);
                training.add(sourceInstance, targetInstance);
            }else{
                String sourceInstance = keys.get( random.nextInt(keys.size()) );
                String targetInstance = getRandomTargetInstance(values, random, refMap, sourceInstance, -1);
//                String sourceInstance = keys.get(i);
//                String targetInstance = values.get(i+3);
                keys.remove(sourceInstance);
                training.add(sourceInstance, targetInstance, 0d);
            }
            }
        }
        return training;
    }
    
    private static String getRandomTargetInstance(List<String> values, Random random, HashMap<String, HashMap<String,Double>> refMap, String sourceInstance, int previousRandom){
                int randomInt;
                do{
                    randomInt = random.nextInt(values.size());
                }while(randomInt == previousRandom);
                
                String tmpTarget = values.get(randomInt);
                if(refMap.get(sourceInstance).get(tmpTarget) == null){
                    return tmpTarget;
                }
                return getRandomTargetInstance(values, random, refMap, sourceInstance, randomInt);
    }

     private static List<LearningParameter> initializeLearningParameters(String className) {
        List<LearningParameter> lParameters = new ArrayList<>() ;
            if(className.equals("WOMBATSIMPLE") || className.equals("WOMBATCOMPLETE")){
                lParameters.add(new LearningParameter("max refinement tree size", 2000, Integer.class, 10, Integer.MAX_VALUE, 10d, "max refinement tree size"));
                lParameters.add(new LearningParameter("max iterations number", 3, Integer.class, 1d, Integer.MAX_VALUE, 10d, "max iterations number"));
                lParameters.add(new LearningParameter("max iteration time in minutes", 20, Integer.class, 1d, Integer.MAX_VALUE,1, "max iteration time in minutes"));
                lParameters.add(new LearningParameter("max execution time in minutes", 600, Integer.class, 1d, Integer.MAX_VALUE,1, "max execution time in minutes"));
                lParameters.add(new LearningParameter("max fitness threshold", 1, Double.class, 0d, 1d, 0.01d, "max fitness threshold"));
                lParameters.add(new LearningParameter("minimum properity coverage", 0.4, Double.class, 0d, 1d, 0.01d, "minimum properity coverage"));
                lParameters.add(new LearningParameter("properity learning rate", 0.9, Double.class, 0d, 1d, 0.01d, "properity learning rate"));
                lParameters.add(new LearningParameter("overall penalty weit", 0.5, Double.class, 0d, 1d, 0.01d, "overall penalty weit"));
                lParameters.add(new LearningParameter("children penalty weit", 1, Double.class, 0d, 1d, 0.01d, "children penalty weit"));
                lParameters.add(new LearningParameter("complexity penalty weit", 1, Double.class, 0d, 1d, 0.01d, "complexity penalty weit"));
                lParameters.add(new LearningParameter("verbose", false, Boolean.class, 0, 1, 0, "verbose"));
                lParameters.add(new LearningParameter("measures", new HashSet<String>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams")), MeasureType.class, 0, 0, 0, "measures"));
                lParameters.add(new LearningParameter("save mapping", true, Boolean.class, 0, 1, 0, "save mapping"));
            }else if(className.equals("DECISIONTREELEARNING")){
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_TRAINING_DATA_SIZE, 10, Integer.class, 1, 100000, 1, DecisionTreeLearning.PARAMETER_TRAINING_DATA_SIZE));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_UNPRUNED_TREE, false, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_UNPRUNED_TREE));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_COLLAPSE_TREE, true, Boolean.class, 0, 1, 1, DecisionTreeLearning.PARAMETER_COLLAPSE_TREE));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_PRUNING_CONFIDENCE, 0.25, Double.class, 0d, 1d, 0.01d, DecisionTreeLearning.PARAMETER_PRUNING_CONFIDENCE));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_REDUCED_ERROR_PRUNING, false, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_REDUCED_ERROR_PRUNING));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_FOLD_NUMBER, 3, Integer.class, 0, 10, 1, DecisionTreeLearning.PARAMETER_FOLD_NUMBER));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_SUBTREE_RAISING, true, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_SUBTREE_RAISING));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_CLEAN_UP, true, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_CLEAN_UP));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_LAPLACE_SMOOTHING, false, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_LAPLACE_SMOOTHING));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_MDL_CORRECTION, true, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_MDL_CORRECTION));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_SEED, 1, Integer.class, 0, 100, 1, DecisionTreeLearning.PARAMETER_SEED));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_PROPERTY_MAPPING, null, PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN, DecisionTreeLearning.PARAMETER_PROPERTY_MAPPING));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_MAPPING, null, AMapping.class, Double.NaN, Double.NaN, Double.NaN, DecisionTreeLearning.PARAMETER_MAPPING));
                lParameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_LINK_SPECIFICATION, null , LinkSpecification.class, Double.NaN, Double.NaN, Double.NaN, DecisionTreeLearning.PARAMETER_LINK_SPECIFICATION));
            }

        return lParameters;

    }

    public void setInitialMapping(AMapping initialMapping) {
        this.initialMapping = initialMapping;
    }


}
