package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
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
    static final int expRuns = 1;
    static int initsize=0;
    private boolean ignoreZeros = false;
    private HashMap<String, Double> alreadySeenLS;
    private HashMap<String, AMapping> alreadyCalculatedMapping;
    private HashSet<String> alreadyChecked;

    static Logger logger = Logger.getLogger(DecisionTreeLearning.class);
    private PropertyMapping propertyMapping;
    private Instances trainingSet;
    private MLResults mlresult;
    private LinkSpecification deltaLS;
    private TreeParser tp;
    private J48 tree;
    private Configuration configuration;
//    private HashSet<SourceTargetValue> previouslyPresentedCandidates;
    private AMapping previouslyPresentedCandidates;
    private ArrayList<SourceTargetValue> base;
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
    private double pruningConfidence = 0.1;
    private boolean reducedErrorPruning = false;
    private int foldNumber = 3;
    private boolean subtreeRaising = true;
    private boolean cleanUp = true;
    private boolean laplaceSmoothing = false;
    private boolean mdlCorrection = true;
    private int seed = 1;
    private AMapping initialMapping = MappingFactory.createDefaultMapping();
    private LinkSpecification bestLS;
    private PseudoFMeasure pfmeasure;
    private double bestFMeasure = 0.0;
    private AMapping prediction;

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
    
    private static final String baseString = "base";
    private static final String negPosString = "negPos";
    private static final String sameSizeString = "sameSize";
    private static final String addAllString = "addAll";
    
    private String mode = addAllString;

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
	    SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		    .getTargetInfo().getVar());
	    mapping = ee.execute(bestLS, dp);
	} else if (this.configuration != null) {
	    logger.info("...by running LinkSpecification from Configuration...");
	    if (this.configuration.getMetricExpression() != null && !this.configuration.getMetricExpression().isEmpty()) {
		bestLS = new LinkSpecification();
		bestLS.readSpec(this.configuration.getMetricExpression(), this.configuration.getAcceptanceThreshold());
		DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
			.getTargetInfo().getVar());
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
					if(sourceCache.getInstance("<" + sourceURI + ">") == null)
					    logger.error("source null");
					if(targetCache.getInstance("<" + targetURI + ">") == null)
					    logger.error("target null");
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
//	logger.info("Mapping size before adding" + oracleMapping.size());
	if(oracleMapping.size() == 0){
	    logger.error("empty oracle Mapping! Returning empty MLResults!");
	    return new MLResults();
	}
	switch(mode){
	case baseString:
	    addBase(oracleMapping);
	    break;
	case negPosString:
//	    oracleMapping = addUntilPositiveNegativeEqual(oracleMapping);
	    break;
	case sameSizeString:
//	    oracleMapping = addUntilSameSize(oracleMapping);
	    break;
	case addAllString:
	    oracleMapping = addAll(oracleMapping);
	    break;
	}
//	logger.info("Mapping size after adding" + oracleMapping.size());
	AMapping originalOracleMapping = MappingFactory.createDefaultMapping();
	// These are the instances labeled by the user so we keep them to not
	// present the same pairs twice
	oracleMapping.getMap().forEach((sourceURI, map2) -> {
	    map2.forEach((targetURI, value) -> {
		previouslyPresentedCandidates.add(sourceURI, targetURI, value);
		originalOracleMapping.add(sourceURI, targetURI, value);
	    });
	});
	   LinkSpecification resLS = buildTreeAndParseToLS(oracleMapping); 
            //If we get the same tree again, raise the threshold to get better results faster
            if(resLS == null){
        	logger.info("Bad tree! Giving the algorithm more information by adding more instances.");
        	if(addBase(oracleMapping)){
        	activeLearn(oracleMapping);
        	}else{
        	    handleUniformTrainingData(oracleMapping);
        	}
            }
        	while(alreadySeenLS.get(resLS.toString()) != null && resLS.getThreshold() != 1.0){
        	    logger.debug("Already seen " + resLS);
        	resLS.setThreshold(Math.min(resLS.getThreshold() + 0.1, 1.0));
        	logger.debug("Setting threshold to: " + resLS.getThreshold());
            }
	    // If we get the same tree again, raise the threshold to get better
	    // results faster
//	    if (bestLS != null) {
//		if (resLS.equals(bestLS)) {
//		    resLS.setThreshold(resLS.getThreshold() + 0.1);
//		}
//	    }
            this.mlresult = new MLResults();
            if(checkIfThereWasBetterLSBefore(resLS)){
        	logger.debug("Already had better LinkSpecification: " + bestLS);
            }else{
            logger.debug("Learned LinkSpecification: " + resLS.toStringOneLine());
            }
            this.mlresult.setLinkSpecification(bestLS);
            this.mlresult.setMapping(prediction);
            this.mlresult.setQuality(bestFMeasure);
            deltaLS = subtractDeltaFromLS(resLS);
	return this.mlresult;
    }
	private LinkSpecification buildTreeAndParseToLS(AMapping oracleMapping) {
	this.trainingSet = createEmptyTrainingInstances(oracleMapping);
	fillInstances(trainingSet, oracleMapping);
	if (!(negativeExample & positiveExample)) {
	    logger.debug("negative examples: " + negativeExample + " positive examples:" + positiveExample);
            negativeExample = false;
            positiveExample = false;
//	    return handleUniformTrainingData(oracleMapping);
            return null;
	}
	LinkSpecification resLS = null;
	try{
	String[] options = getOptionsArray();
	    String previousTreePrefix = "";
	    if(tree != null){
		previousTreePrefix = tree.prefix();
	    }else{
	    }
	    tree = new J48();
	    tree.setOptions(options);
	    logger.info("Building classifier....");
	    tree.buildClassifier(trainingSet);
//	    boolean sameAgain = handleSameTreeAgain(tree, previousTreePrefix, originalOracleMapping);
//            System.err.println(tree.prefix());
//            System.err.println(tree.graph());
            if(tree.prefix().startsWith("[negative ") || tree.prefix().startsWith("[positive ")){
        	logger.info("Bad tree! Giving the algorithm more information by adding more instances.");
        	if(addBase(oracleMapping)){
        	activeLearn(oracleMapping);
        	}else{
        	    handleUniformTrainingData(oracleMapping);
        	}
            }
            logger.info("Parsing tree to LinkSpecification...");
            resLS = treeToLinkSpec(tree);
	}catch(Exception e){
	    e.printStackTrace();
	}
            return resLS;
	}
//    private boolean sameTreeAgain(J48 tree, String previousTreePrefix, AMapping oracleMapping) throws Exception{
//	String modifiedTreePrefix = tree.prefix().replaceAll("\\(\\d+.\\d+\\)", "");
//	String modifiedPreviousTreePrefix = previousTreePrefix.replaceAll("\\(\\d+.\\d+\\)", "");
////	int sizeBefore = oracleMapping.size();
//	if(modifiedTreePrefix.equals(modifiedPreviousTreePrefix)){
////	    logger.info("Same tree as before. Trying to put more emphasis on user input");
////	    addUntilSameSize(oracleMapping);
////	    if(sizeBefore == oracleMapping.size()){
////	    logger.info("Adding Base");
////		addBase(oracleMapping);
////	    }
////	   activeLearn(oracleMapping); 
//	    return true;
//	}
//	return false;
//    }
    
    private boolean checkIfThereWasBetterLSBefore(LinkSpecification ls) {
	if (alreadySeenLS.get(ls.toString()) == null) {
	    logger.debug("Checking: " +ls);
	    MLResults tmp = new MLResults();
	    tmp.setLinkSpecification(ls);
	    AMapping pred = predict(this.sourceCache, this.targetCache, tmp);
	    double pfresult = pfmeasure.calculate(
		    pred,
		    new GoldStandard(null, this.sourceCache.getAllUris(), this.targetCache
			    .getAllUris()));
	    logger.debug("best before: " + bestFMeasure + " now: " + pfresult);
	    alreadySeenLS.put(ls.toString(), pfresult);
	    if (pfresult > bestFMeasure) {
		this.prediction = pred;
		this.bestLS = ls;
		this.bestFMeasure = pfresult;
		return false;
	    }
	}else{
	    logger.debug("already checked: " + ls);
	}
	return true;
    }
    
    private boolean addBase(AMapping oracleMapping){
	boolean changed = false;
	int positive = 0;
	int negative = 0;
        for(String s : oracleMapping.getMap().keySet()){
            for(String t : oracleMapping.getMap().get(s).keySet()){
        	if(oracleMapping.getMap().get(s).get(t) == 1.0){
        	    positive++;
        	}else{
        	    negative++;
        	}
            }
        }
        logger.debug("positive: " + positive + " negative: " + negative);
	if(base.size() != 0){
	    Iterator<SourceTargetValue> it = base.iterator();
	    while(it.hasNext() && positive != negative){
		SourceTargetValue inst = it.next();
		if(inst.value == 1.0){
		    positive++;
		}else{
		    negative++;
		}
		if(!oracleMapping.contains(inst.sourceUri, inst.targetUri)){
		oracleMapping.add(inst.sourceUri, inst.targetUri, inst.value);
		changed = true;
		}
	    }
	}
        logger.debug("positive: " + positive + " negative: " + negative);
	return changed;
    }

    private AMapping addAll(AMapping oracleMapping){
	return ((MemoryMapping)oracleMapping).union(previouslyPresentedCandidates);
    }
    
    private AMapping cloneMapping(AMapping oracleMapping){
	AMapping clonedMapping = MappingFactory.createDefaultMapping();
        for(String s : oracleMapping.getMap().keySet()){
            for(String t : oracleMapping.getMap().get(s).keySet()){
        	double v = oracleMapping.getMap().get(s).get(t);
		clonedMapping.add(s, t, v);
            }
        }
        return clonedMapping;
	
    }
    
    private AMapping balanceInitialMapping(AMapping oracleMapping){
	AMapping originalOracleMapping = MappingFactory.createDefaultMapping();
	int positive = 0;
	int negative = 0;
        for(String s : oracleMapping.getMap().keySet()){
            for(String t : oracleMapping.getMap().get(s).keySet()){
        	double v = oracleMapping.getMap().get(s).get(t);
        	if(v == 1.0){
		originalOracleMapping.add(s, t, v);
        	    positive++;
        	}else{
		originalOracleMapping.add(s, t, v);
        	    negative++;
        	}
            }
        }
        logger.debug("Initial mapping contains  " + positive + " positive and  " + negative + " negative examples");
        if(positive == negative){
            return oracleMapping;
        }else if(positive < negative){
            Iterator<Entry<String, HashMap<String, Double>>> it = oracleMapping.getMap().entrySet().iterator();
            while(it.hasNext()){
        	Entry<String, HashMap<String, Double>> item = it.next();
        	if(item.getValue().values().contains(0.0) && positive != negative){
        	    it.remove();
        	    negative--;
        	}
            }
        }else if(negative < (float)oracleMapping.size()/4.0){
        int tries = 5;
        AMapping bestMapping = MappingFactory.createDefaultMapping();
        while(bestFMeasure < 0.8 && tries != 0){
        int dummynegative = negative;
            logger.debug("Try: " + tries);
           AMapping clonedMapping = cloneMapping(originalOracleMapping); 
            Random rand = new Random();
            while(positive != dummynegative){
        	clonedMapping.add(sourceCache.getAllInstances().get(rand.nextInt(sourceCache.size())).getUri(), targetCache.getAllInstances().get(rand.nextInt(targetCache.size())).getUri(), 0.0);
        	dummynegative++;
            }
		LinkSpecification ls = buildTreeAndParseToLS(clonedMapping);
		if(!checkIfThereWasBetterLSBefore(ls)){
		    bestMapping = clonedMapping;
		}
		tries--;
        }
         return bestMapping;
        }
        logger.debug("Balanced mapping contains  " + positive + " positive and  " + negative + " negative examples");
	return oracleMapping;
    }
    
//    private AMapping addUntilSameSize(AMapping oracleMapping){
//	int positive = 0;
//	int negative = 0;
//        for(String s : oracleMapping.getMap().keySet()){
//            for(String t : oracleMapping.getMap().get(s).keySet()){
//        	if(oracleMapping.getMap().get(s).get(t) == 1.0){
//        	    positive++;
//        	}else{
//        	    negative++;
//        	}
//            }
//        }
//	int leftToAdd = oracleMapping.size();
//	if(previouslyPresentedCandidates.size() != 0){
//	    boolean lastWasPos = false;
//        for(String sourceURI : oracleMapping.getMap().keySet()){
//            for(String targetURI : oracleMapping.getMap().get(sourceURI).keySet()){
//        	double value = oracleMapping.getMap().get(sourceURI).get(targetURI);
//		if (!lastWasPos && value == 1.0 && leftToAdd > 0) {
//		    if (!oracleMapping.contains(sourceURI, targetURI)) {
//			oracleMapping.add(sourceURI, targetURI, value);
//			lastWasPos = true;
//			leftToAdd--;
//			positive++;
//		    }
//		}
//		if (lastWasPos && value != 1.0 && leftToAdd > 0) {
//		    if (!oracleMapping.contains(sourceURI, targetURI)) {
//			oracleMapping.add(sourceURI, targetURI, value);
//			lastWasPos = false;
//			leftToAdd--;
//			negative++;
//		    }
//		}
//            }
//	    }
//	}
//	if(positive < negative){
//	    addBase(oracleMapping);
//	}
//	return oracleMapping;
//	}
    
    

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
	    tp.measuresUsed.clear();
	    ls = tp.parseTreePrefix(treeString);
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
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, this.configuration.getSourceInfo().getVar(), this.configuration
		.getTargetInfo().getVar());
	this.prediction = ee.execute(ls, dp);
	return this.prediction;
    }

    @Override
    public void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
	super.init(lp, sourceCache, targetCache);
	this.previouslyPresentedCandidates = MappingFactory.createDefaultMapping();
	this.base = new ArrayList<SourceTargetValue>();
	this.pfmeasure = new PseudoFMeasure();
	this.negativeExample = false;
	this.positiveExample = false;
	this.trainingSet = null;
	this.bestLS = null;
	this.bestFMeasure = 0.0;
	this.prediction = null;
	alreadySeenLS = new HashMap<String, Double>();
	alreadyCalculatedMapping = new HashMap<String, AMapping>();
	alreadyChecked = new HashSet<String>();
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
		this.bestLS = (LinkSpecification) l.getValue();
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
	return bestLS;
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
	parameters.add(new LearningParameter(PARAMETER_LINK_SPECIFICATION, bestLS, LinkSpecification.class, Double.NaN, Double.NaN, Double.NaN,
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
	AMapping deltaMapping = alreadyCalculatedMapping.get(deltaLS.toString());
	if(deltaMapping == null){
	DynamicPlanner dp = new DynamicPlanner(this.sourceCache, this.targetCache);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(this.sourceCache, this.targetCache, this.configuration.getSourceInfo().getVar(),
		this.configuration.getTargetInfo().getVar());
	deltaMapping = ee.execute(deltaLS, dp);
	alreadyCalculatedMapping.put(deltaLS.toString(), deltaMapping);
	}else{
	    logger.debug("Skip execution because we already calculated this mapping");
	}
	ArrayList<SourceTargetValue> tmpCandidateList = new ArrayList<SourceTargetValue>();
	deltaMapping.getMap().forEach(
		(sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			double compoundMeasureValue = 0;
			for (Map.Entry<String, Double> entry : tp.measuresUsed.entrySet()) {
			    compoundMeasureValue += Math.pow(
				    Math.abs(MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI), targetCache.getInstance(targetURI),
					    entry.getKey(), threshold, "?x", "?y")
					    - entry.getValue()), 2);
			}
			SourceTargetValue candidate = new SourceTargetValue(sourceURI, targetURI, value, compoundMeasureValue);
			if (!previouslyPresentedCandidates.contains(sourceURI, targetURI)) {
			    tmpCandidateList.add(candidate);
			}
		    });
		});

	tmpCandidateList.sort((s1, s2) -> s1.compoundMeasureValue.compareTo(s2.compoundMeasureValue));
//	for(int i = 0; i < 200; i++){
//	    System.out.println(tmpCandidateList.get(i));
//	}

	AMapping mostInformativeLinkCandidates = MappingFactory.createDefaultMapping();

	if(tmpCandidateList.size()!= 0){
	size = (tmpCandidateList.size() < size) ? tmpCandidateList.size() : size;
	for (int i = 0; i < size; i++) {
	    SourceTargetValue candidate = tmpCandidateList.get(i);
	    mostInformativeLinkCandidates.add(candidate.sourceUri, candidate.targetUri, candidate.value);
	}
	for(int i = 1; i <= tmpCandidateList.size(); i++){
	    SourceTargetValue candidate = tmpCandidateList.get(tmpCandidateList.size() - i);
	    candidate.value = 1.0;
	    base.add(candidate);
	}
	}else{
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

    @Override
    protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
	throw new UnsupportedMLImplementationException(this.getName());
    }
    
    public ACache getSourceCache(){
	return this.sourceCache;
    }
    public ACache getTargetCache(){
	return this.targetCache;
    }
    public void setSourceCache(ACache sourceCache){
	this.sourceCache = sourceCache;
    }
    public void setTargetCache(ACache targetCache){
	this.targetCache = targetCache;
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

    public void setInitialMapping(AMapping initialMapping) {
        this.initialMapping = initialMapping;
    }


}
