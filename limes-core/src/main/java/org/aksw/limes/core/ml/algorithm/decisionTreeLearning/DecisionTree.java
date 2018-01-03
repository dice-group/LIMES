package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.io.IOException;
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
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.ErrorEstimatePruning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.PruningFunctionDTL;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.evaluation.DTLEvaluation;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.evaluation.FoldData;
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

	private List<String> calculatePathKeys(DecisionTree node, List<String> pathStrings) {
		if(pathStrings == null)
			pathStrings = new ArrayList<String>();
		if (node.rightChild == null && node.leftChild == null) {
			if (node.root) {
				String path = node.getPathString();
				if (!pathStrings.contains(path)) {
					pathStrings.add(path);
				}
			} else {
				String path = node.getPathString();
				if (!pathStrings.contains(path)) {
					pathStrings.add(path);
				}
			}
		} else if (node.rightChild != null && node.leftChild == null) {
			calculatePathKeys(node.rightChild, pathStrings);
		} else if (node.leftChild != null && node.rightChild == null) {
			// if (!root) {
			String path = node.getPathString();
			if (!pathStrings.contains(path)) {
				pathStrings.add(path);
			}
			// }
			calculatePathKeys(node.leftChild, pathStrings);
		} else {
			calculatePathKeys(node.rightChild, pathStrings);
			calculatePathKeys(node.leftChild, pathStrings);
		}
		return pathStrings;
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
		List<String> paths = calculatePathKeys(getRootNode(), null);
		final String left = "³true³";
		final String right = "³false³";
		String[] pathLS = new String[paths.size()];
		int countPathLS = 0;
		for (String s : paths) {
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
				if(this.parent.rightChild != null){
					parentClone.rightChild = this.parent.rightChild.cloneWithoutParent();
				}
			} else {
				parentClone.rightChild = cloned;
				if(this.parent.leftChild != null){
					parentClone.leftChild = this.parent.leftChild.cloneWithoutParent();
				}
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

	public static void main(String[] args) throws IOException {
		String resultStr = "";
		long start;
		long end;
		int FOLDS_COUNT = 10;
		String[] datasets = { "drugs"};
//				"amazongoogleproducts", "dblpscholar", "abtbuy",
//				"dbplinkedmdb", "person1full", "person2full","drugs", "restaurantsfull" };

		for (String data : datasets) {
			logger.info("\n\n >>>>>>>>>>>>> " + data.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
//			DecisionTreeLearning.useJ48optimized = false;
//			DecisionTreeLearning.useJ48 = false;
        EvaluationData c = DataSetChooser.getData(data);
        List<FoldData>folds = DTLEvaluation.generateFolds(c); 

        FoldData trainData = new FoldData();
        FoldData testData = folds.get(FOLDS_COUNT - 1);
        // perform union on test folds
        for (int i = 0; i < FOLDS_COUNT; i++) {
            if (i != 9) {
                trainData.map = MappingOperations.union(trainData.map,folds.get(i).map);
                trainData.sourceCache = DTLEvaluation.cacheUnion(trainData.sourceCache,folds.get(i).sourceCache);
                trainData.targetCache = DTLEvaluation.cacheUnion(trainData.targetCache,folds.get(i).targetCache);
            }
        }
        // fix caches if necessary
        for (String s : trainData.map.getMap().keySet()) {
            for (String t : trainData.map.getMap().get(s).keySet()) {
                if (!trainData.targetCache.containsUri(t)) {
                    // logger.info("target: " + t);
                    trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
                }
            }
            if (!trainData.sourceCache.containsUri(s)) {
                // logger.info("source: " + s);
                trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
            }
        }
        

        AMapping trainingData = trainData.map;
        ACache trainSourceCache = trainData.sourceCache;
        ACache trainTargetCache = trainData.targetCache;
        ACache testSourceCache = testData.sourceCache;
        ACache testTargetCache = testData.targetCache;

			try {
				AMLAlgorithm dtl = null;
				Configuration config = null;
				MLResults res = null;
				AMapping mapping = null;
				
// ========================================
				DecisionTreeLearning.useJ48 = false;
				logger.info("====NORMAL===");
				dtl = MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				dtl.getMl().setParameter(Eagle.PROPERTY_MAPPING, c.getPropertyMapping());
				start = System.currentTimeMillis();
//				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
//				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
//				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double giErFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==Normal FMeasure: " + giErFM);
				long giErTime = (end - start);
				System.out.println("Time: " + giErTime);
				
				/*
				logger.info("====j48===");
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + c.getSourceCache().size());
				System.out.println("target size: " + c.getTargetCache().size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useJ48 = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
//				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				giErFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==j48 FMeasure: " + giErFM);
				giErTime = (end - start);
				System.out.println("Time: " + giErTime);
				*/
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//			try {
//				AMLAlgorithm dtl = null;
//				Configuration config = null;
//				MLResults res = null;
//				AMapping mapping = null;
//				
//// ========================================
//				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
//						MLImplementationType.SUPERVISED_BATCH);
//				System.out.println("source size: " + c.getSourceCache().size());
//				System.out.println("target size: " + c.getTargetCache().size());
//				dtl.init(null, c.getSourceCache(), c.getTargetCache());
//				config = c.getConfigReader().read();
//				dtl.getMl().setConfiguration(config);
//				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
//				start = System.currentTimeMillis();
//				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
//				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
//				res = dtl.asSupervised().learn(getTrainingData(c.getReferenceMapping()));
//				end = System.currentTimeMillis();
//				System.out.println(res.getLinkSpecification().toStringPretty());
//				mapping = dtl.predict(c.getSourceCache(), c.getTargetCache(), res);
//				double giErFM = new FMeasure().calculate(mapping, new GoldStandard(c.getReferenceMapping()));
//				System.out.println( "\n\n==FMeasure: " + giErFM);
//				long giErTime = (end - start);
//				System.out.println("Time: " + giErTime);
//			} catch (UnsupportedMLImplementationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	public static AMapping getTrainingData(AMapping full) {
		int sliceSizeWanted = full.size() - (int) Math.ceil(((double) full.getSize() / 10.0));
		AMapping slice = MappingFactory.createDefaultMapping();
		Object[] keyArr = full.getMap().keySet().toArray();
		int c = 5;
		int i = 2;
		boolean pos = true;
		while (slice.size() <= sliceSizeWanted) {
			// String key = (String)keyArr[(int)(Math.random() *
			// keyArr.length)];
			String key = (String) keyArr[c];
			c += i;
//			c = (int) (Math.random() * Double.valueOf(keyArr.length));
			if (c >= keyArr.length) {
				c = 0;
				 i++;
			}
			if (!slice.getMap().keySet().contains(key)) {
				if (pos) {
					slice.add(key, full.getMap().get(key));
//					pos = false;
				} else {
					int falsec = 0;
					boolean falseNeg = false;
					while (c == falsec) {
//						double random = Math.random();
//						falsec = (int) (random * keyArr.length);
						falsec = Math.max(0, c - 4);
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
