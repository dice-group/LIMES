package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
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
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnsupervisedDecisionTree {
	protected static Logger logger = LoggerFactory.getLogger(UnsupervisedDecisionTree.class);

	private DecisionTreeLearning dtl;
//	private static List<String> usedMetrics = new ArrayList<String>();
	private static HashMap<String, AMapping> calculatedMappings = new HashMap<String, AMapping>();
	private static HashMap<String, AMapping> pathMappings = new HashMap<String, AMapping>();
	private static HashMap<String, AMapping> nodeMappings = new HashMap<String, AMapping>();
	private static double totalFMeasure = 0.0;
	public static int maxDepth = 0;
	private static UnsupervisedDecisionTree rootNode;

	private ACache originalSourceCache;
	private ACache originalTargetCache;
	// private ACache baseSourceCache;
	// private ACache baseTargetCache;
	// private ACache rightSourceCache;
	// private ACache rightTargetCache;
	// private ACache leftSourceCache;
	// private ACache leftTargetCache;
	private AMapping parentMapping;
	private ExtendedClassifier classifier;
	private UnsupervisedDecisionTree parent;
	private UnsupervisedDecisionTree leftChild;
	private UnsupervisedDecisionTree rightChild;
	private boolean root = false;
	private boolean isLeftNode = false;
	private boolean checked = false;
	private PseudoFMeasure pseudoFMeasure;
	private int depth;

	private double minPropertyCoverage;
	private double propertyLearningRate;

	public UnsupervisedDecisionTree(DecisionTreeLearning dtl, ACache originalSourceCache, ACache originalTargetCache,
			PseudoFMeasure pseudoFMeasure, double minPropertyCoverage, double propertyLearningRate) {
//		usedMetrics = new ArrayList<String>();
		calculatedMappings = new HashMap<String, AMapping>();
		pathMappings = new HashMap<String, AMapping>();
		totalFMeasure = 0.0;
		this.dtl = dtl;
		this.originalSourceCache = originalSourceCache;
		this.originalTargetCache = originalTargetCache;
		// this.baseSourceCache = originalSourceCache;
		// this.baseTargetCache = originalTargetCache;
		this.pseudoFMeasure = pseudoFMeasure;
		this.minPropertyCoverage = minPropertyCoverage;
		this.propertyLearningRate = propertyLearningRate;
		root = true;
		depth = 0;
		rootNode = this;
	}

	private UnsupervisedDecisionTree(DecisionTreeLearning dtl, ACache originalSourceCache, ACache originalTargetCache,
			AMapping parentMapping, PseudoFMeasure pseudoFMeasure, double minPropertyCoverage,
			double propertyLearningRate, UnsupervisedDecisionTree parent, boolean isLeftNode) {
		if (parent == null) {
			logger.error("This constructor is NOT for the root node!!!");
			return;
		}
		this.dtl = dtl;
		this.originalSourceCache = originalSourceCache;
		this.originalTargetCache = originalTargetCache;
		// this.baseSourceCache = baseSourceCache;
		// this.baseTargetCache = baseTargetCache;
		this.parentMapping = parentMapping;
		this.pseudoFMeasure = pseudoFMeasure;
		this.minPropertyCoverage = minPropertyCoverage;
		this.propertyLearningRate = propertyLearningRate;
		this.parent = parent;
		this.isLeftNode = isLeftNode;
		this.root = false;
		this.depth = this.parent.depth + 1;
	}

	public UnsupervisedDecisionTree buildTree(int maxDepth) {
		List<ExtendedClassifier> classifiers = findClassifiers();
		if (classifiers.size() == 0) {
			// logger.info("No classifiers left. Returning null");
			return null;
		}
		Collections.sort(classifiers, Collections.reverseOrder());
		classifier = classifiers.get(0);

		// System.out.println("Depth: " + this.depth);
		if (root) {
			// pathMappings.put(getPathString(),classifier.getMapping());
			totalFMeasure = classifier.getfMeasure();
		} else {

			// if(!check(classifier.getMapping())){
			// return null;
			// }
			// calculatePathMapping(classifier.getMapping());
			if (classifier.getfMeasure() <= 0.1) {
				// System.out.println("NULL");
				return null;
			}
		}
//			String measureExpression = classifier.getMeasure() + "(x." + classifier.getSourceProperty() + ", y."
//					+ classifier.getTargetProperty() + ")";
//			usedMetrics.add(measureExpression);
		if (maxDepth != this.depth) {
			// createCachesFromMapping(classifier.getMapping());
			rightChild = new UnsupervisedDecisionTree(dtl, originalSourceCache, originalTargetCache,
					classifier.getMapping(), pseudoFMeasure, minPropertyCoverage, propertyLearningRate, this, false)
							.buildTree(maxDepth);
			// AMapping lambdaMapping = executeAtomicMeasure(measureExpression,
			// DecisionTreeLearning.threshold);
			// AMapping leftMapping =
			// MappingOperations.difference(lambdaMapping,
			// classifier.getMapping());
			leftChild = new UnsupervisedDecisionTree(dtl, originalSourceCache, originalTargetCache,
					classifier.getMapping(), pseudoFMeasure, minPropertyCoverage, propertyLearningRate, this, true)
							.buildTree(maxDepth);
		}
		return this;
	}

	private boolean check(AMapping current) {
		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		GoldStandard gs = new GoldStandard(null, originalSourceCache.getAllUris(), originalTargetCache.getAllUris());
		double currentFM = 0.0;
		current = MappingOperations.intersection(pathMappings.get(parent.getPathString()), current);
		for (String s : pathMappings.keySet()) {
			AMapping m = pathMappings.get(s);
			if (!s.equals(parent.classifier.getMetricExpression())) {
				current = MappingOperations.union(m, current);
			}
		}
		currentFM = prfm.calculate(current, gs, 0.1);
		if (currentFM >= totalFMeasure) {
			// if(isLeftNode){
			// pathMappings.remove(this.parent.getPathString());
			// }
			pathMappings.put(getPathString(), current);

			totalFMeasure = currentFM;
			return true;
		}
		return false;
	}

	private void calculatePathMapping(AMapping current) {
		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		GoldStandard gs = new GoldStandard(null, originalSourceCache.getAllUris(), originalTargetCache.getAllUris());
		double currentFM = 0.0;
		AMapping parentMapping;
		if (nodeMappings.get(parent.getPathString()) != null) {
			parentMapping = nodeMappings.get(parent.getPathString());
		} else {
			parentMapping = pathMappings.get(parent.getPathString());
			// Since the left childs are calculated after the right childs
			// the parent node can no longer be a leave since it now has two
			// childs
			if (isLeftNode) {
				nodeMappings.put(parent.getPathString(), parentMapping);
				pathMappings.remove(parent.getPathString());
			}
		}
		current = MappingOperations.intersection(parentMapping, current);
		for (AMapping m : pathMappings.values()) {
			current = MappingOperations.union(m, current);
		}
		currentFM = prfm.calculate(current, gs, 0.1);
		pathMappings.put(getPathString(), current);
		totalFMeasure = currentFM;
	}

	public UnsupervisedDecisionTree prune() {
		int currentDepth = maxDepth;
		while (currentDepth != 0) {
			currentDepth--;
			prune2(this, currentDepth);
//			rootPseudoFMeasure();
		}
		return this;
	}

	private UnsupervisedDecisionTree prune2(UnsupervisedDecisionTree root, int depth) {
		if (rightChild == null && leftChild == null) {
			return this;
		}
		// Go to the leaves
		if (rightChild != null && !rightChild.checked) {
			rightChild.prune2(rightChild, depth);
			// rightChild = tmpRightChild;
		}
		if (leftChild != null && !leftChild.checked) {
			leftChild.prune2(leftChild, depth);
			// leftChild = tmpLeftChild;
		}
		if (this.depth - 1 != depth) {
			return this;
		}

		boolean deleteLeft = true;
		boolean deleteRight = true;

		UnsupervisedDecisionTree tmpRightChild = rightChild;
		UnsupervisedDecisionTree tmpLeftChild = leftChild;
		this.rightChild = null;
		double tmp = 0.0;
//		tmp = rootPseudoFMeasure();
		tmp = getTotalPseudoFMeasure(UnsupervisedDecisionTree.rootNode);
		if (tmp > totalFMeasure) {
			deleteRight = false;
		}
		this.rightChild = tmpRightChild;
		this.leftChild = null;
//		tmp = rootPseudoFMeasure();
		tmp = getTotalPseudoFMeasure(UnsupervisedDecisionTree.rootNode);
		if (tmp > totalFMeasure) {
			totalFMeasure = tmp;
			deleteLeft = false;
		}
		this.rightChild = null;
		if (!deleteLeft) {
			this.leftChild = tmpLeftChild;
		}
		if (!deleteRight) {
			this.rightChild = tmpRightChild;
		}
		return this;
	}

	private UnsupervisedDecisionTree prune(UnsupervisedDecisionTree root, int depth) {
		// Go to the leaves
		if (rightChild != null && !rightChild.checked) {
			UnsupervisedDecisionTree tmpRightChild = rightChild.prune(rightChild, depth);
			rightChild = tmpRightChild;
		}
		if (leftChild != null && !leftChild.checked) {
			UnsupervisedDecisionTree tmpLeftChild = leftChild.prune(leftChild, depth);
			leftChild = tmpLeftChild;
		}
		// If tree is atomic there is nothing to prune
		if (root.root) {
			return root;
		}
		if (this.depth != depth) {
			return this;
		}

		// ======= Actual pruning ==============
		// AMapping currentMapping = pathMappings.get(getPathString());
		// PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		// GoldStandard gs = new GoldStandard(null,
		// originalSourceCache.getAllUris(), originalTargetCache.getAllUris());
		// AMapping parentMapping;
		// if(nodeMappings.get(parent.getPathString()) != null){
		// parentMapping = nodeMappings.get(parent.getPathString());
		// }else{
		// parentMapping = pathMappings.get(parent.getPathString());
		// }
		// AMapping totalMapping = parentMapping;
		// for(AMapping m: pathMappings.values()){
		// totalMapping = MappingOperations.union(m, totalMapping);
		// }
		// double newTotalFM = prfm.calculate(totalMapping, gs, 0.1);
		if (isLeftNode) {
			this.parent.leftChild = null;
		} else {
			this.parent.rightChild = null;
		}
		System.out.println(rootNode.toString());
//		double newTotalFM = rootPseudoFMeasure();
		double newTotalFM = getTotalPseudoFMeasure(UnsupervisedDecisionTree.rootNode);
		System.out.println(newTotalFM);
		if (newTotalFM >= totalFMeasure) {
			// pathMappings.remove(getPathString());
			// pathMappings.put(parent.getPathString(),parentMapping);
			totalFMeasure = newTotalFM;
			return null;
		}
		checked = true;
		return this;
	}

	public static double getTotalPseudoFMeasure(UnsupervisedDecisionTree root) {
		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		double pf = prfm.calculate(getTotalMapping(root),
				new GoldStandard(null, root.originalSourceCache.getAllUris(), root.originalTargetCache.getAllUris()),
				0.1);
		pathMappings = new HashMap<String,AMapping>();
		return pf;
	}

	public static AMapping getpath1MAPPING(){
		return pathMappings.get("jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00qgrams(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|0.48jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00");
	}
	
	public static AMapping getpath2MAPPING(){
		return pathMappings.get("jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00qgrams(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|0.48");
	}

	public static AMapping getpath3MAPPING(){
		return pathMappings.get("jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00jaccard(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|1.00");
	}

	public static AMapping getTotalMapping(UnsupervisedDecisionTree root) {
		calculatePathMappings(root);
		AMapping res = MappingFactory.createDefaultMapping();
//		System.out.println("GETTOTALMAPPINGS");
		for (String s : pathMappings.keySet()) {
//			System.out.println(s);
			res = MappingOperations.union(pathMappings.get(s), res);
		}
		return res;
	}

	private static void calculatePathMappings(UnsupervisedDecisionTree node) {
		if (node.rightChild == null && node.leftChild == null) {
			if (node.root) {
				// AMapping res =
				// calculatedMappings.get(node.classifier.getMetricExpression());
				AMapping res = node.classifier.getMapping();
				pathMappings.put(node.getPathString(), res);
			} else {
				putPathMappingsLeaf(node);
			}
		} else if (node.rightChild != null && node.leftChild == null) {
			calculatePathMappings(node.rightChild);
		} else if (node.leftChild != null && node.rightChild == null) {
			putPathMappingsLeaf(node);
			calculatePathMappings(node.leftChild);
		} else {
			calculatePathMappings(node.rightChild);
			calculatePathMappings(node.leftChild);
		}
	}
	
	private static void putPathMappingsLeaf(UnsupervisedDecisionTree node){
				AMapping parent = node.parent.classifier.getMapping();
				AMapping nodeMapping = node.classifier.getMapping();
				if (node.isLeftNode) {
					AMapping res = MappingOperations.difference(nodeMapping, parent);
					pathMappings.put(node.getPathString(), res);
				} else {
					AMapping res = MappingOperations.intersection(nodeMapping, parent);
					pathMappings.put(node.getPathString(), res);
				}
	}

//	private double rootPseudoFMeasure() {
//		LinkSpecification ls = dtl.tp.parseTreePrefix(rootNode.toString());
//		Rewriter rw = RewriterFactory.getDefaultRewriter();
//		ls = rw.rewrite(ls);
//		DynamicPlanner dp = new DynamicPlanner(originalSourceCache, originalTargetCache);
//		SimpleExecutionEngine ee = new SimpleExecutionEngine(originalSourceCache, originalTargetCache,
//				dtl.getConfiguration().getSourceInfo().getVar(), dtl.getConfiguration().getTargetInfo().getVar());
//		AMapping prediction = ee.execute(ls, dp);
//
//		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
//		// return prfm.calculate(rootNode.getPathMapping(), new
//		// GoldStandard(null, originalSourceCache.getAllUris(),
//		// originalTargetCache.getAllUris()),0.1);
//		return prfm.calculate(prediction,
//				new GoldStandard(null, originalSourceCache.getAllUris(), originalTargetCache.getAllUris()), 0.1);
//	}

	// public AMapping getPathMapping(){
	// AMapping rightMapping = MappingFactory.createDefaultMapping();
	// AMapping leftMapping = MappingFactory.createDefaultMapping();
	// if(leftChild == null && rightChild == null){
	// return calculatedMappings.get(classifier.getMetricExpression());
	// }else if(leftChild == null && rightChild != null){
	// rightMapping =
	// MappingOperations.intersection(calculatedMappings.get(classifier.getMetricExpression()),
	// rightChild.getPathMapping());
	// return rightMapping;
	// }else if(rightChild == null && leftChild != null){
	// leftMapping = MappingOperations.difference(leftChild.getPathMapping(),
	// calculatedMappings.get(classifier.getMetricExpression()));
	// return leftMapping;
	// }
	// //if both are not null
	// return MappingOperations.union(rightMapping, leftMapping);
	// }

	private String getPathString() {
		String str = "";
		if (root) {
			if (this.classifier.getMetricExpression() != null && !this.classifier.getMetricExpression().equals("")) {
				str += this.classifier.getMetricExpression();
			}
		} else {
			str += parent.getPathString() + this.classifier.getMetricExpression();
		}
		return str;
	}

	// private void createCachesFromMapping(AMapping mapping){
	// rightSourceCache = new HybridCache();
	// rightTargetCache = new HybridCache();
	// leftSourceCache = ((HybridCache)baseSourceCache).clone();
	// leftTargetCache = ((HybridCache)baseTargetCache).clone();
	// for(String s: mapping.getMap().keySet()){
	// rightSourceCache.addInstance(baseSourceCache.getInstance(s));
	// ((HybridCache)leftSourceCache).removeInstance(s);
	// for(String t: mapping.getMap().get(s).keySet()){
	// rightTargetCache.addInstance(baseTargetCache.getInstance(t));
	// ((HybridCache)leftTargetCache).removeInstance(s);
	// }
	// }
	// }

	/**
	 * @return initial classifiers
	 */
	public List<ExtendedClassifier> findClassifiers() {
		// logger.info("Getting all classifiers ...");
		List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
		// for (PairSimilar<String> propPair :
		// dtl.getPropertyMapping().stringPropPairs) {
		// for (String measure : DecisionTreeLearning.stringMeasures) {
		// ExtendedClassifier cp = findClassifier(propPair.a, propPair.b,
		// measure);
		// if(cp != null)
		// initialClassifiers.add(cp);
		// }
		// }
		// for (PairSimilar<String> propPair :
		// dtl.getPropertyMapping().datePropPairs) {
		// for (String measure : DecisionTreeLearning.dateMeasures) {
		// ExtendedClassifier cp = findClassifier(propPair.a, propPair.b,
		// measure);
		// if(cp != null)
		// initialClassifiers.add(cp);
		// }
		// }
		// for (PairSimilar<String> propPair :
		// dtl.getPropertyMapping().pointsetPropPairs) {
		// for (String measure : DecisionTreeLearning.pointsetMeasures) {
		// ExtendedClassifier cp = findClassifier(propPair.a, propPair.b,
		// measure);
		// if(cp != null)
		// initialClassifiers.add(cp);
		// }
		// }
		for (PairSimilar<String> propPair : dtl.getPropertyMapping().stringPropPairs) {
			for (String measure : DecisionTreeLearning.defaultMeasures) {
				ExtendedClassifier cp = findClassifier(propPair.a, propPair.b, measure);
				if (cp != null)
					initialClassifiers.add(cp);
			}
		}

		// logger.info("Done computing all classifiers.");
		return initialClassifiers;
	}

	private ExtendedClassifier findClassifier(String sourceProperty, String targetProperty, String measure) {
		String measureExpression = measure + "(x." + sourceProperty + ",y." + targetProperty + ")";
		String properties = "(x." + sourceProperty + ",y." + targetProperty + ")";
		ExtendedClassifier cp = new ExtendedClassifier(measure, 0.0, sourceProperty, targetProperty);
//		if (usedMetrics.contains(measureExpression)) {
//			// logger.info("Skipping: " + measureExpression);
//			return null;
//		}
		if(this.parent != null){
		if (this.parent.getPathString().contains(properties)) {
//			logger.info("Skipping: " + properties);
			return null;
		}
		}
		double maxFM = 0.0;
		double theta = 1.0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		GoldStandard gs = new GoldStandard(null, originalSourceCache.getAllUris(), originalTargetCache.getAllUris());
		for (double threshold = 1d; threshold > minPropertyCoverage; threshold = threshold * propertyLearningRate) {
			cp = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
			AMapping mapping = getMeasureMapping(measureExpression, cp);
			// double overlap = recall(mapping);
			// if (maxOverlap < overlap) { //only interested in largest
			// threshold with recall 1
			// bestMapping = mapping;
			// theta = threshold;
			// maxOverlap = overlap;
			// }
			double pfm = prfm.calculate(mapping, gs, 0.1);
			if (maxFM < pfm) { // only interested in largest threshold with
								// highest F-Measure
				bestMapping = mapping;
				theta = threshold;
				maxFM = pfm;
			}
		}
		cp = new ExtendedClassifier(measure, theta, sourceProperty, targetProperty);
		cp.setfMeasure(maxFM);
		cp.setMapping(bestMapping);
		return cp;
	}

	private AMapping getMeasureMapping(String measureExpression, ExtendedClassifier cp) {
		if (this.root) {
			AMapping mapping = executeAtomicMeasure(measureExpression, cp.getThreshold());
			calculatedMappings.put(cp.getMetricExpression(), mapping);
			return mapping;
		}
		AMapping currentClassifierMapping = calculatedMappings.get(cp.getMetricExpression());
		if (this.isLeftNode) {
			AMapping differenceMapping = MappingOperations.difference(currentClassifierMapping, parentMapping);
			// AMapping revdifferenceMapping =
			// MappingOperations.difference(parentMapping,
			// currentClassifierMapping);
			// AMapping union =
			// MappingOperations.union(currentClassifierMapping, parentMapping);
			// AMapping intersection =
			// MappingOperations.intersection(currentClassifierMapping,
			// parentMapping);
			// if(differenceMapping.size() > 0 || union.size() !=
			// parentMapping.size()){
			// System.out.println("currentSize: " +
			// currentClassifierMapping.size());
			// System.out.println("parentSize: " + parentMapping.size());
			// System.out.println("diffSize: " + differenceMapping.size() );
			// System.out.println("revdiffSize: " + revdifferenceMapping.size()
			// );
			// System.out.println("unionSize: " + union.size() );
			// System.out.println("intersectionSize: " + intersection.size() );
			// System.out.println("currentClassifier: " + measureExpression +
			// "|" + threshold);
			// System.out.println("parentClassifier: " +
			// parent.classifier.getMetricExpression());
			// System.out.println("currentMapping = parentMapping: " +
			// currentClassifierMapping.equals(parentMapping));
			// }
			return differenceMapping;
		}
		AMapping joinedMapping = MappingOperations.intersection(currentClassifierMapping, parentMapping);
		return joinedMapping;
	}

	/**
	 * @param sourceProperty
	 *            URI
	 * @param targetProperty
	 *            URI
	 * @param measure
	 *            name
	 * @param threshold
	 *            of the LS
	 * @return Mapping from source to target resources after applying the atomic
	 *         mapper measure(sourceProperty, targetProperty)
	 */
	public AMapping executeAtomicMeasure(String measureExpression, double threshold) {
		// logger.info("Executing: " + measureExpression + "|" + threshold);
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, originalSourceCache,
				originalTargetCache, "?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	public static AMapping execute(String measureExpression, double threshold, ACache sC, ACache tC) {
		// logger.info("Executing: " + measureExpression + "|" + threshold);
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sC, tC, "?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	@Override
	public String toString() {
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

	public static void main(String[] args) {
		String[] datasets = { "person1", "person2", "drugs", "restaurantsfixed" };
//		String data = "drugs";
		 for(String data: datasets){
		EvaluationData c = DataSetChooser.getData(data);

		// AMapping b =
		// execute("qgrams(x.http://www.okkam.org/ontology_person1.owl#soc_sec_id,
		// y.http://www.okkam.org/ontology_person2.owl#soc_sec_id)", 0.48,
		// c.getSourceCache(), c.getTargetCache());
		// AMapping a =
		// execute("qgrams(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,
		// y.http://www.okkam.org/ontology_person2.owl#date_of_birth)", 1.0,
		// c.getSourceCache(), c.getTargetCache());
		// AMapping differenceMapping = MappingOperations.difference(a, b);
		// AMapping revdifferenceMapping = MappingOperations.difference(b, a);
		// AMapping union = MappingOperations.union(a, b);
		// AMapping intersection = MappingOperations.intersection(a, b);
		// System.out.println("a : " + a.size());
		// System.out.println("b : " + b.size());
		// System.out.println("a / b: " + differenceMapping.size() );
		// System.out.println("b / a: " + revdifferenceMapping.size() );
		// System.out.println("a or b: " + union.size() );
		// System.out.println("a and b: " + intersection.size() );
		// System.out.println("a = b: " + a.equals(b));
		// System.exit(0);
		try {
			System.out.println("\n\n >>>>>>>>>>>>> " + data.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
			// System.out.println("\n========WOMBAT==========");
			// AMLAlgorithm wombat =
			// MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
			// MLImplementationType.UNSUPERVISED);
			// wombat.init(null, c.getSourceCache(), c.getTargetCache());
			// wombat.getMl().setConfiguration(c.getConfigReader().read());
			long start = System.currentTimeMillis();
			// MLResults resWom = wombat.asUnsupervised().learn(new
			// PseudoFMeasure());
			long end = System.currentTimeMillis();
			// System.out.println(resWom.getLinkSpecification());
			// System.out.println("FMeasure: " + new
			// FMeasure().calculate(wombat.predict(c.getSourceCache(),
			// c.getTargetCache(), resWom), new
			// GoldStandard(c.getReferenceMapping(),c.getSourceCache(),
			// c.getTargetCache())));
			// System.out.println("Time: " + (end - start));
			System.out.println("========DTL==========");
			AMLAlgorithm dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
					MLImplementationType.UNSUPERVISED);
			dtl.init(null, c.getSourceCache(), c.getTargetCache());
			dtl.getMl().setConfiguration(c.getConfigReader().read());
			((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
			start = System.currentTimeMillis();
			dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
			MLResults res = dtl.asUnsupervised().learn(new PseudoFMeasure());
			end = System.currentTimeMillis();
			System.out.println(res.getLinkSpecification());
			System.out.println(
					"FMeasure: " + new FMeasure().calculate(dtl.predict(c.getSourceCache(), c.getTargetCache(), res),
							new GoldStandard(c.getReferenceMapping(), c.getSourceCache(), c.getTargetCache())));
			System.out.println("Time: " + (end - start));
		AMapping pathMapping = UnsupervisedDecisionTree.getTotalMapping(((DecisionTreeLearning)dtl.getMl()).root);
			System.out.println(
					"Path FMeasure: " + new FMeasure().calculate(pathMapping,
							new GoldStandard(c.getReferenceMapping(), c.getSourceCache(), c.getTargetCache())));
			

		} catch (UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 }
		// double pfDiffOverall = 0.0;
		// double custompfDiffOverall = 0.0;
		//// String data = "drugs";
		// for(String data: datasets){
		// EvaluationData c = DataSetChooser.getData(data);
		// ExecutionEngine ee =
		// ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT,
		// c.getSourceCache(), c.getTargetCache(), "?x", "?y");
		// Configuration config = c.getConfigReader().read();
		// LinkSpecification ls = new
		// LinkSpecification(config.getMetricExpression(),
		// config.getAcceptanceThreshold());
		// if(data.equals("drugs")){
		// ls = new LinkSpecification("jaccard(x.name,y.name)", 1.0);
		// }
		// AMapping predictions = ee.execute(ls, new
		// DynamicPlanner(c.getSourceCache(), c.getTargetCache()));
		// FMeasure fm = new FMeasure();
		// PseudoFMeasure pfm = new PseudoFMeasure();
		// System.out.println("==== " + data + " ====");
		// System.out.println("LS: " + ls);
		// System.out.println("size: " + predictions.size());
		// System.out.println("ref size: " + c.getReferenceMapping().size());
		// double f = fm.calculate(predictions, new
		// GoldStandard(c.getReferenceMapping()));
		// System.out.println("F: " + f);
		// double pf = pfm.calculate(predictions, new GoldStandard(null,
		// c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
		// System.out.println("PF: " + pf);
		// PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		// double customf = prfm.calculate(predictions, new GoldStandard(null,
		// c.getSourceCache().getAllUris(), c.getTargetCache().getAllUris()));
		// System.out.println("Custom: " + customf);
		// double diff = Math.abs(f -pf);
		// pfDiffOverall += diff;
		// System.out.println("diff: " + diff);
		// double cdiff = Math.abs(f -customf);
		// custompfDiffOverall += cdiff;
		// System.out.println("Custom diff: " + cdiff);
		// }
		//
		// System.out.println("\n\n===== OVERALL ========\n\n");
		// System.out.println("pfdiff: " + pfDiffOverall);
		// System.out.println("cpfDiff: " + custompfDiffOverall);
	}

	private static AMapping getTrainingMapping(AMapping refMapping) {
		System.out.println("ref size: " + refMapping.size());
		int size = (int) Math.ceil((double) refMapping.size() / 10.0);
		AMapping res = MappingFactory.createDefaultMapping();
		for (String s : refMapping.getMap().keySet()) {
			if (Math.random() > 0.5) {
				res.add(s, refMapping.getMap().get(s));
				size--;
				if (size <= 0) {
					break;
				}
			}
		}
		System.out.println("Trainging size: " + res.size());
		return res;
	}
}
