package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRefFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.GlobalFMeasure;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.ErrorEstimatePruning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.GlobalFMeasurePruning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.PruningFunctionDTL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTree {
	protected static Logger logger = LoggerFactory.getLogger(DecisionTree.class);

	private DecisionTreeLearning dtl;
	private static HashMap<String, AMapping> calculatedMappings = new HashMap<String, AMapping>();
	private static HashMap<String, AMapping> pathMappings = new HashMap<String, AMapping>();
	public static double totalFMeasure = 0.0;
	public static int maxDepth = 0;
	private static String spaceChar = "︴";

	private ACache sourceCache;
	private ACache targetCache;
	private ACache testSourceCache;
	private ACache testTargetCache;
	private ExtendedClassifier classifier;
	private DecisionTree parent;
	private DecisionTree leftChild;
	private DecisionTree rightChild;
	private boolean root = false;
	private boolean isLeftNode = false;
	private PseudoFMeasure pseudoFMeasure;
	private int depth;

	private double minPropertyCoverage;
	private double propertyLearningRate;
	private double pruningConfidence;

	public static boolean isSupervised = false;
	private AMapping refMapping;
	public static FitnessFunctionDTL fitnessFunction;
	public static PruningFunctionDTL pruningFunction;

	public DecisionTree(DecisionTreeLearning dtl, ACache sourceCache, ACache targetCache, ACache testSourceCache, ACache testTargetCache,PseudoFMeasure pseudoFMeasure,
			double minPropertyCoverage, double propertyLearningRate, double pruningConfidence, AMapping refMapping) {
		calculatedMappings = new HashMap<String, AMapping>();
		pathMappings = new HashMap<String, AMapping>();
		totalFMeasure = 0.0;
		this.dtl = dtl;
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
		this.pseudoFMeasure = pseudoFMeasure;
		this.minPropertyCoverage = minPropertyCoverage;
		this.propertyLearningRate = propertyLearningRate;
		this.pruningConfidence = pruningConfidence;
		root = true;
		depth = 0;
		this.refMapping = refMapping;
		this.testSourceCache = testSourceCache;
		this.testTargetCache = testTargetCache;
	}

	private DecisionTree(DecisionTreeLearning dtl, ACache originalSourceCache, ACache originalTargetCache, ACache testSourceCache, ACache testTargetCache,
		 PseudoFMeasure pseudoFMeasure, double minPropertyCoverage, double pruningConfidence,
			double propertyLearningRate, DecisionTree parent, boolean isLeftNode, AMapping refMapping) {
		this.dtl = dtl;
		this.sourceCache = originalSourceCache;
		this.targetCache = originalTargetCache;
		this.testSourceCache = testSourceCache;
		this.testTargetCache = testTargetCache;
		this.pseudoFMeasure = pseudoFMeasure;
		this.minPropertyCoverage = minPropertyCoverage;
		this.propertyLearningRate = propertyLearningRate;
		this.pruningConfidence = pruningConfidence;
		this.parent = parent;
		this.isLeftNode = isLeftNode;
		this.root = false;
		if (parent != null)
			this.depth = this.parent.depth + 1;
		this.refMapping = refMapping;
	}


	public DecisionTree buildTree(int maxDepth) {
		classifier = fitnessFunction.getBestClassifier(this);
		if (classifier == null)
			return null;
		if (root) {
			totalFMeasure = classifier.getfMeasure();
		} else {
			if (fitnessFunction.stopCondition(this)) {
				return null;
			}
		}
//		System.out.println("Fitness Value: " + classifier.getfMeasure());
		if (maxDepth != this.depth && this.depth < (dtl.getPropertyMapping().getCompletePropMapping().size())) {
			rightChild = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache,
					pseudoFMeasure, minPropertyCoverage, pruningConfidence, propertyLearningRate, this, false, refMapping);
			rightChild = rightChild.buildTree(maxDepth);
			leftChild = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache,
					pseudoFMeasure, minPropertyCoverage, pruningConfidence, propertyLearningRate, this, true, refMapping);
			leftChild = leftChild.buildTree(maxDepth);
		}
		return this;
	}

	public DecisionTree prune() {
		int currentDepth = maxDepth;
		while (currentDepth >= 0) {
			getRootNode().prune(currentDepth);
			currentDepth--;
		}
		return this;
	}

	private DecisionTree prune(int depth) {
		if (rightChild == null && leftChild == null) {
			return this;
		}
		// Go to the leaves
		if (rightChild != null) {
			rightChild.prune(depth);
		}
		if (leftChild != null) {
			leftChild.prune(depth);
		}
		if (this.depth != depth) {
			return this;
		}
		return pruningFunction.pruneChildNodesIfNecessary(this);
	}

	private DecisionTree getRootNode() {
		if (!root) {
			if (parent == null) {
				logger.error("Detached node!Cannot get root! Returning null!");
				return null;
			}
			return parent.getRootNode();
		}
		return this;
	}

	public AMapping getTotalMapping() {
		// pathMappings = new HashMap<String, AMapping>();
		DecisionTree rootNode = getRootNode();
		List<String> pathStrings = calculatePathMappings(rootNode);
		AMapping res = MappingFactory.createDefaultMapping();
		Iterator<String> it = pathMappings.keySet().iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (!pathStrings.contains(s)) {
				it.remove();
			} else {
				res = MappingOperations.union(pathMappings.get(s), res);
			}
		}
		return res;
	}

	private List<String> calculatePathMappings(DecisionTree node) {
		List<String> pathStrings = new ArrayList<String>();
		if (node.rightChild == null && node.leftChild == null) {
			if (node.root) {
				AMapping res = node.classifier.getMapping();
				String path = node.getPathString();
				if (!pathMappings.keySet().contains(path)) {
					pathMappings.put(path, res);
					pathStrings.add(path);
				}
			} else {
				String path = node.getPathString();
				if (!pathMappings.keySet().contains(path)) {
					pathMappings.put(path, node.getPathMapping());
					pathStrings.add(path);
				}
			}
		} else if (node.rightChild != null && node.leftChild == null) {
			calculatePathMappings(node.rightChild);
		} else if (node.leftChild != null && node.rightChild == null) {
			// if (!root) {
			String path = node.getPathString();
			if (!pathMappings.keySet().contains(path)) {
				pathMappings.put(path, node.getPathMapping());
				pathStrings.add(path);
			}
			// }
			calculatePathMappings(node.leftChild);
		} else {
			calculatePathMappings(node.rightChild);
			calculatePathMappings(node.leftChild);
		}
		return pathStrings;
	}

	public AMapping getPathMapping() {
		if (root) {
			return classifier.getMapping();
		}
		AMapping parentMapping = this.parent.getPathMapping();
		AMapping nodeMapping = classifier.getMapping();
		AMapping res = null;
		if (isLeftNode) {
			res = MappingOperations.difference(nodeMapping, parentMapping);
		} else {
			res = MappingOperations.intersection(nodeMapping, parentMapping);
		}
		return res;
	}

	public String getPathString() {
		String str = "" + depth;
		if (root) {
			if (this.classifier.getMetricExpression() != null && !this.classifier.getMetricExpression().equals("")) {
				str += spaceChar + this.classifier.getMetricExpression();
			}
		} else {
			if (isLeftNode) {
				str += "left";
			} else {
				str += "right";
			}
			str += parent.getPathString() + spaceChar + "³" + isLeftNode + "³" + this.classifier.getMetricExpression();
		}
		return str;
	}

	public LinkSpecification getTotalLS() {
		pathMappings = new HashMap<String, AMapping>();
		calculatePathMappings(getRootNode());
		final String left = "³true³";
		final String right = "³false³";
		String[] pathLS = new String[pathMappings.keySet().size()];
		int countPathLS = 0;
		for (String s : pathMappings.keySet()) {
			double outerThreshold = 0.0;
			String[] path = s.split(spaceChar);
			String lsString = "";
			for (int i = 1; i < path.length; i++) {
				if (path[i].startsWith(left)) {
					lsString = "MINUS(" + path[i] + "," + lsString + ")";
					lsString += "|0.0";
				} else if (path[i].startsWith(right)) {
					lsString = "AND(" + path[i] + "," + lsString + ")";
					lsString += "|0.0";
				} else {
					if (lsString.equals("")) {
						// path[0] is always the shortcode for the path e.g.
						// 2left1right0
						if (path.length == 2) {
							// Split on | because this an atomic ls and we use
							// the rest as threshold
							if (path[i].contains("|")) {
								outerThreshold = Double.parseDouble(path[i].split("\\|")[1]);
							}
						}
						lsString = path[i];
					} else {
						if (path[i + 1].startsWith(right)) {
							lsString = "AND(" + path[i + 1] + "," + path[i] + ")";
						} else {
							lsString = "MINUS(" + path[i + 1] + "," + path[i] + ")";
						}
					}
				}
			}
			lsString = lsString.replaceAll(left, "");
			lsString = lsString.replaceAll(right, "");
			LinkSpecification ls = new LinkSpecification(lsString, outerThreshold);
			AMapping predMapping = dtl.predict(testSourceCache, testTargetCache, new MLResults(ls, null, 0, null));
			AMapping pathMapping = pathMappings.get(s);
			boolean same = predMapping.size() == pathMapping.size();
			if (!same) {
				System.err.println("\n\n ====== ! ! ! ! ! DIFFERENT !!!!!! ====== \n\n");
				System.err.println("PredMapping: " + predMapping.size());
				System.err.println("PathMapping: " + pathMapping.size());
			}
			lsString += "|" + outerThreshold;
			pathLS[countPathLS] = lsString;
			countPathLS++;
		}
		String finalLSString = "";
		LinkSpecification finalLS = null;
		if (pathLS.length == 1) {
			// Split on last | to get threshold
			int index = pathLS[0].lastIndexOf("|");
			String[] lsStringArr = { pathLS[0].substring(0, index), pathLS[0].substring(index + 1) };
			return new LinkSpecification(lsStringArr[0], Double.parseDouble(lsStringArr[1]));
		}
		for (int i = 0; i < pathLS.length; i++) {
			if (i == 0) {
				finalLSString += "OR(" + pathLS[i] + "," + pathLS[i + 1] + ")";
				i++;
			} else {
				finalLSString = "OR(" + finalLSString + "," + pathLS[i] + ")";
			}
			if (i == (pathLS.length - 1)) {
				finalLS = new LinkSpecification(finalLSString, 0.0);
			} else {
				finalLSString += "|0.0";
			}
		}
		return finalLS;
	}

	@Override
	public DecisionTree clone() {
		DecisionTree cloned = null;
		if (this.root) {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence,
					propertyLearningRate, refMapping);
			cloned.classifier = new ExtendedClassifier(classifier.getMeasure(), classifier.getThreshold(),
					classifier.getSourceProperty(), classifier.getTargetProperty());
			cloned.depth = depth;
			if (rightChild != null) {
				cloned.rightChild = this.rightChild.cloneWithoutParent();
			}
			if (leftChild != null) {
				cloned.leftChild = this.leftChild.cloneWithoutParent();
			}
		} else {
			DecisionTree parentClone = this.parent.cloneWithoutChild(isLeftNode);
			cloned = this.cloneWithoutParent();
			if (this.isLeftNode) {
				parentClone.leftChild = cloned;
				parentClone.rightChild = this.parent.rightChild.cloneWithoutParent();
			} else {
				parentClone.rightChild = cloned;
				parentClone.leftChild = this.parent.leftChild.cloneWithoutParent();
			}
		}
		return cloned;
	}

	protected DecisionTree cloneWithoutParent() {
		DecisionTree cloned = null;
		if (root) {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence,
					propertyLearningRate, refMapping);
		} else {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache, pseudoFMeasure,
					minPropertyCoverage, pruningConfidence, propertyLearningRate, null, isLeftNode, refMapping);
		}
		cloned.classifier = new ExtendedClassifier(classifier.getMeasure(), classifier.getThreshold(),
				classifier.getSourceProperty(), classifier.getTargetProperty());
		cloned.depth = depth;
		if (leftChild != null) {
			DecisionTree leftClone = leftChild.cloneWithoutParent();
			leftClone.parent = cloned;
			cloned.leftChild = leftClone;
		}
		if (rightChild != null) {
			DecisionTree rightClone = rightChild.cloneWithoutParent();
			rightClone.parent = cloned;
			cloned.rightChild = rightClone;
		}
		return cloned;
	}

	protected DecisionTree cloneWithoutChild(boolean withoutLeft) {
		DecisionTree cloned = null;
		DecisionTree parentClone = null;
		if (this.parent != null) {
			parentClone = this.parent.cloneWithoutChild(isLeftNode);
		}
		if (root) {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence,
					propertyLearningRate, refMapping);
		} else {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, testSourceCache, testTargetCache, pseudoFMeasure,
					minPropertyCoverage, pruningConfidence, propertyLearningRate, parentClone, isLeftNode, refMapping);
		}
		cloned.classifier = new ExtendedClassifier(classifier.getMeasure(), classifier.getThreshold());
		cloned.depth = depth;
		if (parentClone != null) {
			if (this.isLeftNode) {
				parentClone.leftChild = cloned;
				parentClone.rightChild = this.parent.rightChild.cloneWithoutParent();
			} else {
				parentClone.rightChild = cloned;
				parentClone.leftChild = this.parent.leftChild.cloneWithoutParent();
			}
		}
		return cloned;
	}

	public double calculateFMeasure(AMapping mapping, AMapping refMap) {
		double res = 0.0;
		if (isSupervised) {
			GoldStandard gs = new GoldStandard(refMap, testSourceCache.getAllUris(), testTargetCache.getAllUris());
			FMeasure fm = new FMeasure();
			res = fm.calculate(mapping, gs);
		} else {
			GoldStandard gs = new GoldStandard(null, sourceCache.getAllUris(), targetCache.getAllUris());
			PseudoRefFMeasure prfm = new PseudoRefFMeasure();
			res = prfm.calculate(mapping, gs);
		}
		return res;
	}

	public AMapping getMeasureMapping(String measureExpression, ExtendedClassifier cp) {
		if (this.root) {
			AMapping mapping = executeAtomicMeasure(measureExpression, cp.getThreshold());
			calculatedMappings.put(cp.getMetricExpression(), mapping);
			return mapping;
		}
		classifier = cp;
		AMapping classifierMapping = calculatedMappings.get(cp.getMetricExpression());
		if (classifierMapping == null) {
			classifierMapping = executeAtomicMeasure(measureExpression, cp.getThreshold());
			calculatedMappings.put(cp.getMetricExpression(), classifierMapping);
		}
		classifier.setMapping(classifierMapping);
		return getTotalMapping();
	}

	public AMapping executeAtomicMeasure(String measureExpression, double threshold) {
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, testSourceCache,
				testTargetCache, "?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	public String toStringOneLine() {
		String res = "";
		if (classifier != null) {
			res += classifier.getMeasure() + TreeParser.delimiter + classifier.getSourceProperty() + "|"
					+ classifier.getTargetProperty() + ": <= " + classifier.getThreshold() + ", > "
					+ classifier.getThreshold() + "[";
			if (leftChild != null) {
				res += leftChild.toString();
			} else {
				res += "negative (0)";
			}
			res += "][";
			if (rightChild != null) {
				res += rightChild.toString();
			} else {
				res += "positive (0)";
			}
			res += "]";
		} else {
			res += "Classifier not set yet";
		}
		return res;
	}

	@Override
	public String toString() {
		String res = "\n";
		res += new String(new char[depth]).replace("\0", "\t");
		if (classifier != null) {
			res += classifier.getMeasure() + TreeParser.delimiter + classifier.getSourceProperty() + "|"
					+ classifier.getTargetProperty() + ": <= " + classifier.getThreshold() + ", > "
					+ classifier.getThreshold() + "[";
			if (leftChild != null) {
				res += leftChild.toString();
			} else {
				res += "negative (0)";
			}
			res += "][";
			if (rightChild != null) {
				res += rightChild.toString();
			} else {
				res += "positive (0)";
			}
			res += "]";
		} else {
			res += "Classifier not set yet";
		}
		return res;
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String resultStr = "";
		long start;
		long end;
		final int EUCLID_ITERATIONS = 20;
		final double MIN_COVERAGE = 0.6d;
		String[] datasets = {
				"dbplinkedmdb", "person1full", "person2full","drugs", "restaurantsfull" };
		// String data = "person2";

//	 for(int k = 0; k < 10; k++){
//		PrintWriter writer = new PrintWriter(new FileOutputStream("/home/ohdorno/Documents/Arbeit/DecisionTrees/smallexperiments/smallresults"+k+".csv",false));
//		String header = "Data\tWombat\tMandC\tGlobE\tGlobGl\tGiniGl\tGiniE\tj48\tj48opt\n";
//		writer.write(header);
//		String dataline = "";
//		PrintWriter writerTime = new PrintWriter(new FileOutputStream("/home/ohdorno/Documents/Arbeit/DecisionTrees/smallexperiments/smallresultstime"+k+".csv",false));
//		String headerTime = "Data\tWombat\tMandC\tGlobE\tGlobGl\tGiniGl\tGiniE\tj48\tj48opt\n";
//		writerTime.write(headerTime);
//		String datalineTime = "";
		for (String data : datasets) {
			DecisionTreeLearning.useJ48optimized = false;
			DecisionTreeLearning.useJ48 = false;
			EvaluationData c = DataSetChooser.getData(data);

			System.out.println("\n\n >>>>>>>>>>>>> " + data.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
			AMapping trainingData = getTrainingData(c.getReferenceMapping());
//			dataline += data.replace("full", "");
			// Training phase
			// System.out.println("\n========EUCLID==========");
			//
			// LinearMeshSelfConfigurator lsc = null;
			// Cache sourceCache = new HybridCache();
			// Cache targetCache = new HybridCache();
			// for(Instance i : c.getSourceCache().getAllInstances()){
			// de.uni_leipzig.simba.data.Instance newI = new
			// de.uni_leipzig.simba.data.Instance(i.getUri());
			// for (String property : i.getAllProperties()) {
			// newI.addProperty(property, i.getProperty(property));
			// }
			// sourceCache.addInstance(newI);
			// }
			// for(Instance i : c.getTargetCache().getAllInstances()){
			// de.uni_leipzig.simba.data.Instance newI = new
			// de.uni_leipzig.simba.data.Instance(i.getUri());
			// for (String property : i.getAllProperties()) {
			// newI.addProperty(property, i.getProperty(property));
			// }
			// targetCache.addInstance(newI);
			// }
			// lsc = new LinearMeshSelfConfigurator(sourceCache, targetCache,
			// MIN_COVERAGE, 1d);
			// // logger.info("Running " + euclideType);
			// lsc.setMeasure(new PseudoMeasures());
			// long begin = System.currentTimeMillis();
			// // logger.info("Computing simple classifiers...");
			// List<SimpleClassifier> cp = lsc.getBestInitialClassifiers();
			// ComplexClassifier cc = lsc.getZoomedHillTop(5, EUCLID_ITERATIONS,
			// cp);
			// long durationMS = System.currentTimeMillis() - begin;
			// Mapping learnedMap = cc.mapping;
			// learnedMap.initReversedMap();
			// String metricExpr = cc.toString();
			// System.out.println("Learned: " + metricExpr);
			//
			// resultStr +=
			// // PRFCalculator.precision(learnedMap, trainData.map) + "\t" +
			// // PRFCalculator.recall(learnedMap, trainData.map) + "\t" +
			// // PRFCalculator.fScore(learnedMap, trainData.map) + "\t" +
			// durationMS + "\t";
			// // +
			// // metricExpr + "\t" ;
			//
			// // Test phase
			// lsc.setSource(sourceCache);
			// lsc.setTarget(targetCache);
			// begin = System.currentTimeMillis();
			// Mapping oldlearnedTestMap = lsc.getMapping(cc.classifiers);
			// AMapping learnedTestMap = MappingFactory.createDefaultMapping();
			// for(String s: oldlearnedTestMap.map.keySet()){
			// learnedTestMap.add(s, oldlearnedTestMap.map.get(s));
			// }
			// GoldStandard gs = new GoldStandard(c.getReferenceMapping(),
			// c.getSourceCache().getAllUris(),
			// c.getTargetCache().getAllUris());
			// resultStr += new Precision().calculate(learnedTestMap, gs) + "\t"
			// + new Recall().calculate(learnedTestMap, gs) + "\t"
			// + new FMeasure().calculate(learnedTestMap, gs) + "\n";
			// // + "\t" +
			// // durationMS + "\n" ;
			// System.out.println(resultStr);
			// for(int i = 0; i < 10; i++){
			// System.out.println("\n========EAGLE==========");
			// AMLAlgorithm eagle =
			// MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
			// MLImplementationType.UNSUPERVISED);
			// eagle.init(null, c.getSourceCache(), c.getTargetCache());
			// eagle.getMl().setConfiguration(c.getConfigReader().read());
			// eagle.getMl().setParameter(Eagle.PROPERTY_MAPPING,
			// c.getPropertyMapping());
			// long start = System.currentTimeMillis();
			// MLResults resEagle = eagle.asUnsupervised().learn(new
			// PseudoFMeasure());
			// long end = System.currentTimeMillis();
			// System.out.println(resEagle.getLinkSpecification());
			// System.out.println("Learned size: " +
			// eagle.predict(c.getSourceCache(), c.getTargetCache(),
			// resEagle).size());
			// System.out.println("FMeasure: "
			// + new FMeasure().calculate(eagle.predict(c.getSourceCache(),
			// c.getTargetCache(), resEagle),
			// new GoldStandard(c.getReferenceMapping(), c.getSourceCache(),
			// c.getTargetCache())));
			// System.out.println("Time: " + (end - start));
			// }

			try {
				AMLAlgorithm dtl = null;
				Configuration config = null;
				MLResults res = null;
				AMapping mapping = null;
/*
				System.out.println("\n========WOMBAT==========");
				AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH);
				wombat.init(null, c.getSourceCache(), c.getTargetCache());
				wombat.getMl().setConfiguration(c.getConfigReader().read());
				start = System.currentTimeMillis();
				MLResults resWom = wombat.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();

				double womFM = new FMeasure().calculate(wombat.predict(c.getSourceCache(), c.getTargetCache(), resWom),
								new GoldStandard(c.getReferenceMapping(), c.getSourceCache(), c.getTargetCache()));
				System.out.println(resWom.getLinkSpecification());
				System.out.println("FMeasure: " + womFM);
				long womTime = (end - start);
				System.out.println("Time: " + womTime);
				System.out.println("========DTL==========");
				
				dtl  = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useMergeAndConquer = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double MaCFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(), c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println( "\n\n==Merge and Conquer ==\nFMeasure: " + MaCFM);
				long MaCTime = (end - start);
				System.out.println("Time: " + MaCTime);
				DecisionTreeLearning.useMergeAndConquer = false;
				*/
//==================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double GErFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(), c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println( "\n\n==Global + ErrorEstimate==\nFMeasure: " + GErFM);
				long GErTime = (end - start);
				System.out.println("Time: " + GErTime);
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double GGFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(), c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println(
						"\n\n==Global + Global==\nFMeasure: " + GGFM);
				long GGTime = (end - start);
				System.out.println("Time: " + GGTime);

// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double giGFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(), c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println( "\n\n==Gini + Global==\nFMeasure: " + giGFM);
				long giGTime = (end - start);
				System.out.println("Time: " + giGTime);
				
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double giErFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(),
								c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println( "\n\n==Gini + ErrorEstimate==\nFMeasure: " + giErFM);
				long giErTime = (end - start);
				System.out.println("Time: " + giErTime);
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useJ48 = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double j48FM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(),
								c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println( "\n\n==J48==\nFMeasure: " + j48FM);
				long j48Time = (end - start);
				System.out.println("Time: " + j48Time);
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, c.getSourceCache(), c.getTargetCache());
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useJ48 = true;
				DecisionTreeLearning.useJ48optimized = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
				double j48optFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping(),
								c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
				System.out.println( "\n\n==j48optimized==\nFMeasure: " + j48optFM);
				long j48optTime = (end - start);
				System.out.println("Time: " + j48optTime);
				DecisionTreeLearning.useJ48 = false;
				DecisionTreeLearning.useJ48optimized = false;
//				dataline += data + "\t"+ womFM + "\t" +MaCFM + "\t" + GErFM + "\t" + GGFM + "\t" + giGFM + "\t" + giErFM +"\t" + j48FM + "\t" + j48optFM + "\n";
//				writer.write(dataline);
//				dataline = "";
//
//				datalineTime += data + "\t"+ womTime + "\t" +MaCTime + "\t" + GErTime + "\t" + GGTime + "\t" + giGTime + "\t" + giErTime +"\t" + j48Time + "\t" + j48optTime + "\n";
//				writerTime.write(datalineTime);
//				datalineTime = "";
				// Nach dem Prunen sollte bei Bäumen öfter sowas rauskommen bei
				// person1
				// LinkSpecification ls = new
				// LinkSpecification("AND(jaccard(x.http://www.okkam.org/ontology_person1.owl#soc_sec_id,y.http://www.okkam.org/ontology_person2.owl#soc_sec_id)|1.0,
				// qgrams(x.http://www.okkam.org/ontology_person1.owl#has_address,y.http://www.okkam.org/ontology_person2.owl#has_address)|0.8222222222222222)",0.0);
				// AMapping testMapping = dtl.predict(c.getSourceCache(),
				// c.getTargetCache(), new MLResults(ls, null, 0.0, null));
				// System.out.println("FMeasure: "
				// + new FMeasure().calculate(testMapping,
				// new GoldStandard(c.getReferenceMapping(),
				// c.getSourceCache().getAllUris(),
				// c.getTargetCache().getAllUris())));
				/*
				 * System.out.println(" ====== Not in ref mapping ====== ");
				 * for(String s: mapping.getMap().keySet()){ for(String t:
				 * mapping.getMap().get(s).keySet()){ if(!refMapping.contains(s,
				 * t)){ System.out.println(c.getSourceCache().getInstance(s).
				 * getProperty(
				 * "http://www.okkam.org/ontology_person1.owl#has_address") +
				 * " = " + c.getTargetCache().getInstance(t).getProperty(
				 * "http://www.okkam.org/ontology_person2.owl#has_address") +
				 * " : " +
				 * MeasureProcessor.getSimilarity(c.getSourceCache().getInstance
				 * (s), c.getTargetCache().getInstance(t),
				 * "qgrams(x.http://www.okkam.org/ontology_person1.owl#has_address,y.http://www.okkam.org/ontology_person2.owl#has_address)",
				 * 0.01, "?x", "?y")); } } }
				 * 
				 * System.out.println("\n\n\n ====== Not in mapping ====== ");
				 * for(String s: refMapping.getMap().keySet()){ for(String t:
				 * refMapping.getMap().get(s).keySet()){ if(!mapping.contains(s,
				 * t)){ System.out.println(c.getSourceCache().getInstance(s).
				 * getProperty(
				 * "http://www.okkam.org/ontology_person1.owl#has_address") +
				 * " = " + c.getTargetCache().getInstance(t).getProperty(
				 * "http://www.okkam.org/ontology_person2.owl#has_address") +
				 * " : " +
				 * MeasureProcessor.getSimilarity(c.getSourceCache().getInstance
				 * (s), c.getTargetCache().getInstance(t),
				 * "qgrams(x.http://www.okkam.org/ontology_person1.owl#has_address,y.http://www.okkam.org/ontology_person2.owl#has_address)",
				 * 0.01, "?x", "?y")); } } }
				 */

				/*
				 * LinkSpecification ls = res.getLinkSpecification(); double
				 * threshold = 0.1; double bestfm = 0.0; double bestThreshold =
				 * 0.0; while (threshold <= 1.0){ ls.setThreshold(threshold);
				 * double fm = new
				 * FMeasure().calculate(dtl.predict(c.getSourceCache(),
				 * c.getTargetCache(), res),new
				 * GoldStandard(c.getReferenceMapping(),
				 * c.getSourceCache().getAllUris(),
				 * c.getTargetCache().getAllUris())); if(fm > bestfm){ bestfm =
				 * fm; bestThreshold = threshold; }
				 * System.out.println("FMeasure: " + fm + "\t threshold: "
				 * +threshold); threshold = threshold + 0.01; }
				 * System.out.println("BEST FMeasure: " + bestfm +
				 * "\t threshold: " + bestThreshold);
				 */
				// AMapping pathMapping =
				// DecisionTree.getTotalMapping(((DecisionTreeLearning)
				// dtl.getMl()).root);
				// System.out.println("Path FMeasure: " + new
				// FMeasure().calculate(pathMapping,
				// new GoldStandard(c.getReferenceMapping(), c.getSourceCache(),
				// c.getTargetCache())));

			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		writer.close();
//		writerTime.close();
//		}
	}

	public static AMapping getTrainingData(AMapping full) {
		int sliceSizeWanted = full.size() - (int) Math.ceil(((double) full.getSize() / 10.0));
		AMapping slice = MappingFactory.createDefaultMapping();
		Object[] keyArr = full.getMap().keySet().toArray();
		int c = 5;
		// int i = 2;
		boolean pos = true;
		while (slice.size() <= sliceSizeWanted) {
			// String key = (String)keyArr[(int)(Math.random() *
			// keyArr.length)];
			String key = (String) keyArr[c];
			c = (int) (Math.random() * Double.valueOf(keyArr.length));
			if (c >= keyArr.length) {
				c = 0;
				// i++;
			}
			if (!slice.getMap().keySet().contains(key)) {
				if (pos) {
					slice.add(key, full.getMap().get(key));
//					pos = false;
				} else {
					int falsec = 0;
					boolean falseNeg = false;
					while (c == falsec) {
						double random = Math.random();
						falsec = (int) (random * keyArr.length);
					}
					HashMap<String, Double> tmpMap = full.getMap().get((String) keyArr[falsec]);
					HashMap<String, Double> falseMap = new HashMap<String, Double>();
					for (String t : tmpMap.keySet()) {
						if (full.contains(key, t)) {
							falseNeg = true;
						}
						falseMap.put(t, 0.0);
					}
					if (!falseNeg) {
						slice.add(key, falseMap);
						pos = true;
					}
				}
			}
			// for(String s: slice.getMap().keySet()){
			// for(String t: slice.getMap().get(s).keySet()){
			// if(full.getMap().get(s).get(t) != null &&
			// full.getMap().get(s).get(t) != slice.getMap().get(s).get(t)){
			// System.err.println("WRONG!!!");
			// }
			// }
			// }
		}
		System.out.println("got: " + MappingOperations.intersection(slice,full).size() + " wanted: " + sliceSizeWanted
				+ " full: " + full.size());
		return slice;
	}

	public void setRefMapping(AMapping refMapping) {
		this.refMapping = refMapping;
	}

	public AMapping getRefMapping() {
		return refMapping;
	}

	public double getMinPropertyCoverage() {
		return minPropertyCoverage;
	}

	public double getPropertyLearningRate() {
		return propertyLearningRate;
	}

	public ACache getSourceCache() {
		return sourceCache;
	}

	public ACache getTargetCache() {
		return targetCache;
	}

	public ACache getTestSourceCache() {
		return testSourceCache;
	}

	public ACache getTestTargetCache() {
		return testTargetCache;
	}

	public ExtendedClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(ExtendedClassifier classifier) {
		this.classifier = classifier;
	}

	public DecisionTree getParent() {
		return parent;
	}

	public DecisionTreeLearning getDtl() {
		return dtl;
	}

	public boolean isLeftNode() {
		return isLeftNode;
	}

	public boolean isRoot() {
		return root;
	}

	public DecisionTree getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(DecisionTree leftChild) {
		this.leftChild = leftChild;
	}

	public DecisionTree getRightChild() {
		return rightChild;
	}

	public void setRightChild(DecisionTree rightChild) {
		this.rightChild = rightChild;
	}

	public void setParent(DecisionTree parent) {
		this.parent = parent;
	}

	public double getPruningConfidence() {
		return pruningConfidence;
	}

	public void setPruningConfidence(double pruningConfidence) {
		this.pruningConfidence = pruningConfidence;
	}

}
