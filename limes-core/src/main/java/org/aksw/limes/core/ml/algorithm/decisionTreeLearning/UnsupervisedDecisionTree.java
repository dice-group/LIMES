package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnsupervisedDecisionTree {
	protected static Logger logger = LoggerFactory.getLogger(UnsupervisedDecisionTree.class);

	private DecisionTreeLearning dtl;
	private static HashMap<String, AMapping> calculatedMappings = new HashMap<String, AMapping>();
	private static HashMap<String, AMapping> pathMappings = new HashMap<String, AMapping>();
	private static double totalFMeasure = 0.0;
	public static int maxDepth = 0;
	private static UnsupervisedDecisionTree rootNode;

	private ACache sourceCache;
	private ACache targetCache;
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

	public UnsupervisedDecisionTree(DecisionTreeLearning dtl, ACache sourceCache, ACache targetCache,
			PseudoFMeasure pseudoFMeasure, double minPropertyCoverage, double propertyLearningRate) {
		calculatedMappings = new HashMap<String, AMapping>();
		pathMappings = new HashMap<String, AMapping>();
		totalFMeasure = 0.0;
		this.dtl = dtl;
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
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
		this.sourceCache = originalSourceCache;
		this.targetCache = originalTargetCache;
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
			return null;
		}
		Collections.sort(classifiers, Collections.reverseOrder());
		classifier = classifiers.get(0);

		if (root) {
			totalFMeasure = classifier.getfMeasure();
		} else {

			if (classifier.getfMeasure() <= 0.1) {
				return null;
			}
		}
		if (maxDepth != this.depth) {
			rightChild = new UnsupervisedDecisionTree(dtl, sourceCache, targetCache,
					classifier.getMapping(), pseudoFMeasure, minPropertyCoverage, propertyLearningRate, this, false)
							.buildTree(maxDepth);
			leftChild = new UnsupervisedDecisionTree(dtl, sourceCache, targetCache,
					classifier.getMapping(), pseudoFMeasure, minPropertyCoverage, propertyLearningRate, this, true)
							.buildTree(maxDepth);
		}
		return this;
	}

	public UnsupervisedDecisionTree prune() {
		int currentDepth = maxDepth;
		while (currentDepth != 0) {
			currentDepth--;
			prune(this, currentDepth);
		}
		return this;
	}

	private UnsupervisedDecisionTree prune(UnsupervisedDecisionTree root, int depth) {
		if (rightChild == null && leftChild == null) {
			return this;
		}
		// Go to the leaves
		if (rightChild != null && !rightChild.checked) {
			rightChild.prune(rightChild, depth);
		}
		if (leftChild != null && !leftChild.checked) {
			leftChild.prune(leftChild, depth);
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
		tmp = getTotalPseudoFMeasure(UnsupervisedDecisionTree.rootNode);
		if (tmp > totalFMeasure) {
			deleteRight = false;
		}
		this.rightChild = tmpRightChild;
		this.leftChild = null;
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


	public static double getTotalPseudoFMeasure(UnsupervisedDecisionTree root) {
		AMapping totalMapping = getTotalMapping(root);
		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		double pf = prfm.calculate(totalMapping,
				new GoldStandard(null, root.sourceCache.getAllUris(), root.targetCache.getAllUris()),
				0.1);
		pathMappings = new HashMap<String,AMapping>();
		return pf;
	}

	public static AMapping getTotalMapping(UnsupervisedDecisionTree root) {
		calculatePathMappings(root);
		AMapping res = MappingFactory.createDefaultMapping();
		for (String s : pathMappings.keySet()) {
			res = MappingOperations.union(pathMappings.get(s), res);
		}
		return res;
	}

	private static void calculatePathMappings(UnsupervisedDecisionTree node) {
		if (node.rightChild == null && node.leftChild == null) {
			if (node.root) {
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

	/**
	 * @return initial classifiers
	 */
	public List<ExtendedClassifier> findClassifiers() {
		// logger.info("Getting all classifiers ...");
		List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
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
		if(this.parent != null){
		if (this.parent.getPathString().contains(properties)) {
			return null;
		}
		}
		double maxFM = 0.0;
		double theta = 1.0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		GoldStandard gs = new GoldStandard(null, sourceCache.getAllUris(), targetCache.getAllUris());
		for (double threshold = 1d; threshold > minPropertyCoverage; threshold = threshold * propertyLearningRate) {
			cp = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
			AMapping mapping = getMeasureMapping(measureExpression, cp);
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
			return differenceMapping;
		}
		AMapping joinedMapping = MappingOperations.intersection(currentClassifierMapping, parentMapping);
		return joinedMapping;
	}

	public AMapping executeAtomicMeasure(String measureExpression, double threshold) {
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache,
				targetCache, "?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	public static AMapping execute(String measureExpression, double threshold, ACache sC, ACache tC) {
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

		try {
			System.out.println("\n\n >>>>>>>>>>>>> " + data.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
			 System.out.println("\n========WOMBAT==========");
			 AMLAlgorithm wombat =
			 MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
			 MLImplementationType.UNSUPERVISED);
			 wombat.init(null, c.getSourceCache(), c.getTargetCache());
			 wombat.getMl().setConfiguration(c.getConfigReader().read());
			long start = System.currentTimeMillis();
			 MLResults resWom = wombat.asUnsupervised().learn(new
			 PseudoFMeasure());
			long end = System.currentTimeMillis();
			 System.out.println(resWom.getLinkSpecification());
			 System.out.println("FMeasure: " + new
			 FMeasure().calculate(wombat.predict(c.getSourceCache(),
			 c.getTargetCache(), resWom), new
			 GoldStandard(c.getReferenceMapping(),c.getSourceCache(),
			 c.getTargetCache())));
			 System.out.println("Time: " + (end - start));
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
	}
}
