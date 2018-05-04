package org.aksw.limes.core.ml.algorithm.dragon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.PruningFunctionDTL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTree {
	protected static Logger logger = LoggerFactory.getLogger(DecisionTree.class);

	private Dragon dtl;
	private static HashMap<String, AMapping> calculatedMappings = new HashMap<String, AMapping>();
	private static HashMap<String, AMapping> pathMappings = new HashMap<String, AMapping>();
	public static double totalFMeasure = 0.0;
	public static int maxDepth = 0;
	private static String spaceChar = "︴";
	private static final String delimiter = "§";

	private ACache sourceCache;
	private ACache targetCache;
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

	private AMapping refMapping;
	public static FitnessFunctionDTL fitnessFunction;
	public static PruningFunctionDTL pruningFunction;

	public DecisionTree(Dragon dtl, ACache sourceCache, ACache targetCache,PseudoFMeasure pseudoFMeasure,
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
	}

	private DecisionTree(Dragon dtl, ACache sourceCache, ACache targetCache, PseudoFMeasure pseudoFMeasure, double minPropertyCoverage, double pruningConfidence,
			double propertyLearningRate, DecisionTree parent, boolean isLeftNode, AMapping refMapping) {
		this.dtl = dtl;
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
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
		if (maxDepth != this.depth && this.depth < (dtl.getPropertyMapping().getCompletePropMapping().size())) {
			rightChild = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence, propertyLearningRate, this, false, refMapping);
			rightChild = rightChild.buildTree(maxDepth);
			leftChild = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence, propertyLearningRate, this, true, refMapping);
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
		DecisionTree rootNode = getRootNode();
		List<String> pathStrings = calculatePathMappings(rootNode);
		if(rootNode != null){
			assert pathStrings.size() > 0;
		}
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
			String path = node.getPathString();
			if (!pathStrings.contains(path)) {
				pathStrings.add(path);
			}
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
				}
				pathStrings.add(path);
			} else {
				String path = node.getPathString();
				if (!pathMappings.keySet().contains(path)) {
					pathMappings.put(path, node.getPathMapping());
				}
				pathStrings.add(path);
			}
		} else if (node.rightChild != null && node.leftChild == null) {
			pathStrings.addAll(calculatePathMappings(node.rightChild));
		} else if (node.leftChild != null && node.rightChild == null) {
			String path = node.getPathString();
			if (!pathMappings.keySet().contains(path)) {
				pathMappings.put(path, node.getPathMapping());
			}
			pathStrings.add(path);
			pathStrings.addAll(calculatePathMappings(node.leftChild));
		} else {
			pathStrings.addAll(calculatePathMappings(node.rightChild));
			pathStrings.addAll(calculatePathMappings(node.leftChild));
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
			cloned = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence,
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
			cloned = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence,
					propertyLearningRate, refMapping);
		} else {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure,
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
			cloned = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure, minPropertyCoverage, pruningConfidence,
					propertyLearningRate, refMapping);
		} else {
			cloned = new DecisionTree(dtl, sourceCache, targetCache, pseudoFMeasure,
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
		GoldStandard gs = new GoldStandard(refMap, dtl.getTestSourceCache().getAllUris(), dtl.getTestTargetCache().getAllUris());
		FMeasure fm = new FMeasure();
		res = fm.calculate(mapping, gs);
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
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache, "?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	public String toStringOneLine() {
		String res = "";
		if (classifier != null) {
			res += classifier.getMeasure() + delimiter + classifier.getSourceProperty() + "|"
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
			res += classifier.getMeasure() + delimiter + classifier.getSourceProperty() + "|"
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

	public ExtendedClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(ExtendedClassifier classifier) {
		this.classifier = classifier;
	}

	public DecisionTree getParent() {
		return parent;
	}

	public Dragon getDtl() {
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
