package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
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
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.aksw.limes.core.util.ParenthesisMatcher;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import com.google.common.collect.Table;

public class DecisionTreeLearning extends ACoreMLAlgorithm {

    static Logger logger = Logger.getLogger("LIMES");
    private PropertyMapping propertyMapping;
    private Instances trainingSet;
    private MLModel mlresult;
    private LinkSpecification defaultLS = new LinkSpecification();
    private TreeParser tp;
    private J48 tree;
    private Configuration configuration;
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
	if (this.configuration.getMetricExpression() != null && !this.configuration.getMetricExpression().isEmpty()) {
	    defaultLS.readSpec(this.configuration.getMetricExpression(), this.configuration.getAcceptanceThreshold());
	    DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
	    SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		    .getTargetInfo().getVar());
	    mapping = ee.execute(defaultLS, dp);
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

	public SourceTargetValue(String s, String t, double v) {
	    sourceUri = s;
	    targetUri = t;
	    value = v;
	}

	@Override
	public String toString() {
	    return sourceUri + " -> " + targetUri + " : " + value;
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
    protected MLModel activeLearn() throws UnsupportedMLImplementationException {
	AMapping trainingData = generateTrainingSet();
	return activeLearn(trainingData);
    }

    @Override
    protected MLModel activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
	this.trainingSet = createEmptyTrainingInstances(oracleMapping);
	fillInstances(trainingSet, oracleMapping, true);
	LinkSpecification ls = new LinkSpecification();
	String[] options = getOptionsArray();
	tree = new J48(); 
	try {
	    tree.setOptions(options);
	    tree.buildClassifier(trainingSet);
	    ls = treeToLinkSpec(tree);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	MLModel res = new MLModel();
	res.setLinkSpecification(ls);
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
	    ls = tp.parseTreePrefix(treeString);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return ls;
    }

    @Override
    public AMapping predict(Cache source, Cache target, MLModel mlModel) {
	LinkSpecification ls = mlresult.getLinkSpecification();
	DynamicPlanner dp = new DynamicPlanner(sourceCache, targetCache);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache, this.configuration.getSourceInfo().getVar(), this.configuration
		.getTargetInfo().getVar());
	return ee.execute(ls, dp);
    }

    @Override
    public void init(List<LearningParameter> lp, Cache sourceCache, Cache targetCache) {
	super.init(lp, sourceCache, targetCache);
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

    private void classifyUnlabeled(HashMap<Instance, SourceTargetValue> instMap) {
	Iterator<Entry<Instance, SourceTargetValue>> it = instMap.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry<Instance, SourceTargetValue> pair = (Map.Entry) it.next();
	    double clsLabel = 0.0;
	    try {
		clsLabel = tree.classifyInstance(pair.getKey());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    pair.getKey().setClassValue(clsLabel);
	    it.remove(); // avoids a ConcurrentModificationException
	}
    }

    public static void main(String[] args) {
	// String configFile =
	// "/home/ohdorno/Dokumente/Arbeit/LIMES/Examples/GeneticEval/Amazon-GoogleProducts.xml";
	// XMLConfigurationReader reader = new XMLConfigurationReader();
	// Configuration config = reader.read(configFile);
	// config.getSourceInfo()
	// .setEndpoint(config.getSourceInfo().getEndpoint());
	// config.getTargetInfo()
	// .setEndpoint(config.getTargetInfo().getEndpoint());
	//
	// Cache sC = HybridCache.getData(config.getSourceInfo());
	//
	// HybridCache tC = HybridCache.getData(config.getTargetInfo());
	//
	// DecisionTreeLearning dtl = new DecisionTreeLearning(sC, tC, config);
	//
	// ActiveLearningSetting setting = new ActiveLearningSetting(dtl);
	// PropertyMapping propMap = new PropertyMapping();
	// propMap.addStringPropertyMatch("title", "name");
	// propMap.addStringPropertyMatch("description", "description");
	// propMap.addStringPropertyMatch("manufacturer", "manufacturer");
	// propMap.addNumberPropertyMatch("price", "price");
	// setting.setPropMap(propMap);
	// dtl.propertyMapping = propMap;
	//
	// Mapping mapping = dtl.generateTrainingSet();
	// try {
	// dtl.init(setting, mapping);
	// System.out.println("learning...");
	// dtl.learn(mapping);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String res = "";
	// for (DataSets d : DataSetChooser.DataSets.values()) {
	// if (!d.toString().equals("RESTAURANTS_FIXED") &&
	// !d.toString().startsWith("OAEI")) {
	// String datasetName = d.toString();
	String datasetName = DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString();
	EvaluationData evalData = DataSetChooser.getData(datasetName);
	Configuration config = evalData.getConfigReader().read();
	DecisionTreeLearning dtl = new DecisionTreeLearning(config);
	dtl.propertyMapping = evalData.getPropertyMapping();
	MLModel model = null;
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
	System.out.println();
	System.out.println(model.getLinkSpecification().toString());
	System.out.println("predicting....");
	AMapping mapping = dtl.predict(evalData.getSourceCache(), evalData.getTargetCache(), model);
	// System.out.println("printin...");
	mapping.getMap().forEach((sourceURI, map2) -> {
	    map2.forEach((targetURI, value) -> {
		// System.out.println(sourceURI + " -> " + targetURI + " :" +
		// value);
		if (value < dtl.lowest) {
		    dtl.lowest = value;
		}
		if (value > dtl.highest) {
		    dtl.highest = value;
		}
	    });
	});
	System.out.println("lowest: " + dtl.lowest + " highest: " + dtl.highest);

	// AMapping randomMapping =
	// dtl.getRandomMapping(evalData.getSourceCache(),
	// evalData.getTargetCache());
	// Instances unlabeled =
	// dtl.createEmptyTrainingInstances(randomMapping);
	// HashMap<Instance, SourceTargetValue> instMap =
	// dtl.fillInstances(unlabeled, randomMapping, false);
	// dtl.classifyUnlabeled(instMap);
	// Iterator<Entry<Instance, SourceTargetValue>> it =
	// instMap.entrySet().iterator();
	// while (it.hasNext()) {
	// Map.Entry<Instance, SourceTargetValue> pair = (Map.Entry)it.next();
	// System.out.println(pair.getKey().classAttribute().value((int)pair.getKey().classValue())
	// + " = " + pair.getValue());
	// it.remove(); // avoids a ConcurrentModificationException
	// }

	// Evaluator evaluator = new Evaluator();
	// String datasetName =
	// DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString();
	// EvaluationData evalData = DataSetChooser.getData(datasetName);
	// Set<TaskData> tasks = new TreeSet<TaskData>();
	// TaskData task = new TaskData();
	// GoldStandard gs = new GoldStandard(evalData.getReferenceMapping());
	// task = new TaskData(gs, evalData.getSourceCache(),
	// evalData.getTargetCache());
	// task.dataName = datasetName;
	// tasks.add(task);
	//
	// Set<EvaluatorType> evaluators=new TreeSet<EvaluatorType>();
	//
	// evaluators.add(EvaluatorType.PRECISION);
	// evaluators.add(EvaluatorType.RECALL);
	// evaluators.add(EvaluatorType.F_MEASURE);
	// evaluators.add(EvaluatorType.P_PRECISION);
	// evaluators.add(EvaluatorType.P_RECALL);
	// evaluators.add(EvaluatorType.PF_MEASURE);
	// evaluators.add(EvaluatorType.ACCURACY);
	//
	// Configuration config = evalData.getConfigReader().read();
	// AMLAlgorithm algo = null;
	// try {
	// algo =
	// MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
	// MLImplementationType.SUPERVISED_ACTIVE);
	// } catch (UnsupportedMLImplementationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// ((DecisionTreeLearning)algo.getMl()).setPropertyMapping(evalData.getPropertyMapping());
	// ((DecisionTreeLearning)algo.getMl()).setConfiguration(config);
	// Table<String, String, Map<EvaluatorType, Double>> results =
	// evaluator.crossValidate(algo, tasks,10, evaluators, null);
	// System.out.println(results.toString());

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
    protected MLModel learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
	return mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    protected MLModel learn(AMapping trainingData) throws UnsupportedMLImplementationException {
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
