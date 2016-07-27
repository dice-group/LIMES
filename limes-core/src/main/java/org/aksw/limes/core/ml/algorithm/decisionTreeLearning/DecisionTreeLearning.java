package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.util.ParenthesisMatcher;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTreeLearning extends ACoreMLAlgorithm {

    static Logger logger = Logger.getLogger("LIMES");
    private PropertyMapping propertyMapping;
    private Instances trainingSet;
    private MLResults mlresult;
    private LinkSpecification defaultLS = new LinkSpecification();
    private LinkSpecification deltaLS;
    private TreeParser tp;
    private J48 tree;
    private Configuration configuration;
    private HashSet<SourceTargetValue> previouslyPresentedCandidates;
    // TODO delete this
    public double lowest = 1.0;
    public double highest = 0.0;

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

    // Default parameters
    private int trainingDataSize = 100;
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

    public DecisionTreeLearning() {
	super();
	this.tp = new TreeParser(this);
    }

    public DecisionTreeLearning(Configuration c) {
	super();
	this.configuration = c;
	this.tp = new TreeParser(this);
    }

    /**
     * Generates training set out of config if there is a linkspec
     * 
     * @return mapping of executed linkspec
     */
    public AMapping generateTrainingSet() {
	AMapping mapping = MappingFactory.createDefaultMapping();
	if(this.configuration != null){
	    if (this.configuration.getMetricExpression() != null && !this.configuration.getMetricExpression().isEmpty()) {
                defaultLS.readSpec(this.configuration.getMetricExpression(), this.configuration.getAcceptanceThreshold());
                DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
                SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
                    .getTargetInfo().getVar());
                mapping = ee.execute(defaultLS, dp);
            }
	}else{
	    mapping = getRandomMapping(sourceCache, targetCache);
	}
	// TODO implement fallback solution
	return mapping;
    }

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
    private HashMap<Instance, SourceTargetValue> fillInstances(Instances trainingSet, AMapping mapping, boolean label) {
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
				inst.setValue(i, MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI), targetCache.getInstance(targetURI),
					metricExpression, threshold, "?x", "?y"));
			    } else {
				if (label) {
				    String classValue = "negative";
				    if (value == 1.0) {
					classValue = "positive";
				    }
				    inst.setValue(i, classValue);
				}
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

    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
	AMapping trainingData = generateTrainingSet();
	return activeLearn(trainingData);
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
	//These are the instances labeled by the so we keep them to not present the same pairs twice
	oracleMapping.getMap().forEach(
		(sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			previouslyPresentedCandidates.add(new SourceTargetValue(sourceURI, targetURI, value));
		    });
		});
	this.trainingSet = createEmptyTrainingInstances(oracleMapping);
	fillInstances(trainingSet, oracleMapping, true);
	String[] options = getOptionsArray();
	tree = new J48(); 
	try {
	    tree.setOptions(options);
	    tree.buildClassifier(trainingSet);
	    deltaLS = treeToLinkSpec(tree);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	MLResults res = new MLResults();
	res.setLinkSpecification(this.getLSwithoutDelta(deltaLS));
	this.mlresult = res;
	return res;
    }
    
    private String[] getOptionsArray(){
	ArrayList<String> tmpOptions = new ArrayList<>();
	if(unprunedTree){
	    tmpOptions.add("-U");
	}
	if(!collapseTree){
	    tmpOptions.add("-C");
	}
	//default value in J48 is 0.25
	if(pruningConfidence != 0.25){
	    tmpOptions.add("-C");
	    tmpOptions.add(String.valueOf(pruningConfidence));
	}
	if(reducedErrorPruning){
	    tmpOptions.add("-R");
	}
	//default value in J48 is 3
	if(foldNumber != 3){
	    tmpOptions.add("-N");
	    tmpOptions.add(String.valueOf(foldNumber));
	}
	if(!subtreeRaising){
	    tmpOptions.add("-S");
	}
	if(!cleanUp){
	    tmpOptions.add("-L");
	}
	if(laplaceSmoothing){
	    tmpOptions.add("-A");
	}
	if(!mdlCorrection){
	    tmpOptions.add("-J");
	}
	//default value in J48 is 1
	if(seed != 1){
	    tmpOptions.add("-Q");
	    tmpOptions.add(String.valueOf(seed));
	}
	String[] options = new String[tmpOptions.size()];
	return tmpOptions.toArray(options);
    }

    private LinkSpecification treeToLinkSpec(J48 tree) {
	LinkSpecification ls = new LinkSpecification();
	try {
	    String treeString = tree.prefix().substring(1, ParenthesisMatcher.findMatchingParenthesis(tree.prefix(), 0));
	    TreeParser.measuresUsed.clear();
	    ls = tp.parseTreePrefix(treeString);
	    System.out.println("Threshold: " + ls.getThreshold());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return ls;
    }

    @Override
    public AMapping predict(Cache source, Cache target, MLResults mlModel) {
	LinkSpecification ls = mlresult.getLinkSpecification();
	DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		.getTargetInfo().getVar());
	return ee.execute(ls, dp);
    }

    @Override
    public void init(List<LearningParameter> lp, Cache sourceCache, Cache targetCache) {
	super.init(lp, sourceCache, targetCache);
	previouslyPresentedCandidates = new HashSet<SourceTargetValue>();
	if(lp == null){
	    setDefaultParameters();
	}else{
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
		this.pruningConfidence = (Integer) l.getValue();
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
	    default:
		logger.error("Unknown parameter name. Setting defaults!");
		setDefaultParameters();
		break;
	    }
	}
    }
    
    
    private LinkSpecification getLSwithoutDelta(LinkSpecification ls){
	if(ls.getMeasure().startsWith("MINUS")){
	    return getLSwithoutDelta(ls, true);
	}
	return getLSwithoutDelta(ls, false);
    }
    private LinkSpecification getLSwithoutDelta(LinkSpecification ls, boolean parentIsMinus){
	if(ls.isAtomic()){
	    if(!parentIsMinus){
		if(ls.getThreshold() != 0){
		ls.setThreshold((BigDecimal.valueOf(ls.getThreshold()).subtract(BigDecimal.valueOf(TreeParser.delta))).doubleValue()); //have to use BigDecimal because of floating numbers magic
		}
		return ls;
	    }else{
		ls.setThreshold((BigDecimal.valueOf(ls.getThreshold()).add(BigDecimal.valueOf(TreeParser.delta))).doubleValue());
		return ls;
	    }
	}
	ArrayList<LinkSpecification>newChildren = new ArrayList<LinkSpecification>();
	if(ls.getMeasure().startsWith("MINUS")){
	    for(LinkSpecification l : ls.getChildren()){
		newChildren.add(getLSwithoutDelta(l, true));
	    }
	    ls.setChildren(newChildren);
	}else{
	    for(LinkSpecification l : ls.getChildren()){
		newChildren.add(getLSwithoutDelta(l, false));
	    }
	    ls.setChildren(newChildren);
	}
	return ls;
    }
    
    private AMapping getRandomMapping(Cache sC, Cache tC) {
	AMapping m = MappingFactory.createDefaultMapping();
	logger.info("Get random initial training data.");
	sC.resetIterator();
	tC.resetIterator();
	while (m.size() < trainingDataSize) {
	    org.aksw.limes.core.io.cache.Instance inst1 = sC.getNextInstance();
	    org.aksw.limes.core.io.cache.Instance inst2 = tC.getNextInstance();
	    if (inst1 != null && inst2 != null) {
		m.add(inst1.getUri(), inst2.getUri(), 0d);
	    }
	}
	return m;
    }

    public static void main(String[] args) {
	String datasetName = DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString();
	EvaluationData evalData = DataSetChooser.getData(datasetName);
	Configuration config = evalData.getConfigReader().read();
	DecisionTreeLearning dtl = new DecisionTreeLearning(config);
	dtl.propertyMapping = evalData.getPropertyMapping();
	MLResults model = null;
	try {
	    dtl.init(new ArrayList<LearningParameter>(), evalData.getSourceCache(), evalData.getTargetCache());
	    System.out.println("learning...");
	    model = dtl.activeLearn();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    System.out.println(dtl.tree.prefix());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    dtl.getNextExamples(10);
	} catch (UnsupportedMLImplementationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println();
	System.out.println(model.getLinkSpecification().toString());
//	System.out.println("\n measures used: \n");
//	Iterator it = TreeParser.measuresUsed.entrySet().iterator();
//	    while (it.hasNext()) {
//	        Map.Entry pair = (Map.Entry)it.next();
//	        System.out.println(pair.getKey() + " = " + pair.getValue());
//	        it.remove(); // avoids a ConcurrentModificationException
//	    }
//	System.out.println();
//	System.out.println("predicting....");
//	AMapping mapping = dtl.predict(evalData.getSourceCache(), evalData.getTargetCache(), model);
//	// System.out.println("printin...");
//	mapping.getMap().forEach((sourceURI, map2) -> {
//	    map2.forEach((targetURI, value) -> {
//		// System.out.println(sourceURI + " -> " + targetURI + " :" +
//		// value);
//		if (value < dtl.lowest) {
//		    dtl.lowest = value;
//		}
//		if (value > dtl.highest) {
//		    dtl.highest = value;
//		}
//	    });
//	});
//	System.out.println("lowest: " + dtl.lowest + " highest: " + dtl.highest);

//	LinkSpecification ls1 = new LinkSpecification("MINUS(jaccard(x.title,y.title)|0.9,cosine(x.title,y.title)|0.4)",0.0);
//	System.out.println(dtl.getLSwithoutDelta(ls1, false));
//	DynamicPlanner dp = new DynamicPlanner(evalData.getSourceCache(), evalData.getTargetCache());
//	SimpleExecutionEngine ee = new SimpleExecutionEngine(evalData.getSourceCache(), evalData.getTargetCache(), config.getSourceInfo().getVar(), config
//		.getTargetInfo().getVar());
//	AMapping mapping = ee.execute(ls1, dp);
//	System.out.println("ls1: " + mapping.getNumberofMappings());
//	mapping.getMap().forEach((sourceURI, map2) -> {
//	    map2.forEach((targetURI, value) -> {
//		// System.out.println(sourceURI + " -> " + targetURI + " :" +
//		// value);
//		if (value < dtl.lowest) {
//		    dtl.lowest = value;
//		}
//		if (value > dtl.highest) {
//		    dtl.highest = value;
//		}
//	    });
//	});
//	System.out.println("lowest: " + dtl.lowest + " highest: " + dtl.highest);

    }

    public LinkSpecification getDefaultLS() {
	return defaultLS;
    }

    @Override
    public void setDefaultParameters() {
	parameters = new ArrayList<>();
	parameters.add(new LearningParameter(PARAMETER_TRAINING_DATA_SIZE, trainingDataSize, Integer.class, 10d, 1000, 10d, PARAMETER_TRAINING_DATA_SIZE));
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
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
	return mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
	DynamicPlanner dp = new DynamicPlanner(this.sourceCache, this.targetCache);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(this.sourceCache, this.targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		.getTargetInfo().getVar());
	AMapping deltaMapping = ee.execute(deltaLS, dp);
	ArrayList<SourceTargetValue> tmpCandidateList = new ArrayList<SourceTargetValue>();
	deltaMapping.getMap().forEach(
		(sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			//compound measure is sum of |measure(s,t) - threshold of used measure|^2
			double compoundMeasureValue = 0;
			for (Map.Entry<String, Double> entry : TreeParser.measuresUsed.entrySet()) {
			    compoundMeasureValue += Math.pow(Math.abs(MeasureProcessor.getSimilarity(sourceCache.getInstance(sourceURI), targetCache.getInstance(targetURI),
					entry.getKey(), threshold, "?x", "?y") - entry.getValue()),2);
			}
			SourceTargetValue candidate = new SourceTargetValue(sourceURI, targetURI, value, compoundMeasureValue);
			if(!previouslyPresentedCandidates.contains(candidate)){
			    tmpCandidateList.add(candidate);
			}
		    });
		});
	tmpCandidateList.sort((s1,s2) -> s1.compoundMeasureValue.compareTo(s2.compoundMeasureValue));
	
	//TODO delete this
	AMapping mostInformativeLinkCandidates = MappingFactory.createDefaultMapping();
	for(int i = 0; i < size; i++){
	    SourceTargetValue candidate = tmpCandidateList.get(i);
	    mostInformativeLinkCandidates.add(candidate.sourceUri, candidate.targetUri, candidate.value);
	}
	return mostInformativeLinkCandidates;
    }

    @Override
    protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
	// TODO Auto-generated method stub
	return null;
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

}
