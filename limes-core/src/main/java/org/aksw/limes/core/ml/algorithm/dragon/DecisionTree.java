package org.aksw.limes.core.ml.algorithm.dragon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTree {
	protected static Logger logger = LoggerFactory.getLogger(DecisionTree.class);

	private final Dragon dtl;
	private static Map<String, AMapping> calculatedMappings = new HashMap<>();
	private static Map<String, AMapping> pathMappings = new HashMap<>();
	public static double totalFMeasure = 0.0;
	public static int maxDepth = 0;
	private static String spaceChar = "︴";
	private static final String delimiter = "§";

	private final ACache sourceCache;
	private final ACache targetCache;
	private ExtendedClassifier classifier;
	private DecisionTree parent;
	private DecisionTree leftChild;
	private DecisionTree rightChild;
	private boolean root = false;
	private boolean isLeftNode = false;
	private final PseudoFMeasure pseudoFMeasure;
	private int depth;

	private final double minPropertyCoverage;
	private final double propertyLearningRate;
	private double pruningConfidence;

	private AMapping refMapping;
	public static FitnessFunctionDTL fitnessFunction;
	public static PruningFunctionDTL pruningFunction;
	private PropertyMapping propertyMapping;

	public DecisionTree(Dragon dtl, ACache sourceCache, ACache targetCache, PseudoFMeasure pseudoFMeasure,
			double minPropertyCoverage, double propertyLearningRate, double pruningConfidence, AMapping refMapping,
			PropertyMapping propertyMapping) {
		calculatedMappings = new HashMap<>();
		pathMappings = new HashMap<>();
		totalFMeasure = 0.0;
		this.dtl = dtl;
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
		this.pseudoFMeasure = pseudoFMeasure;
		this.minPropertyCoverage = minPropertyCoverage;
		this.propertyLearningRate = propertyLearningRate;
		this.pruningConfidence = pruningConfidence;
		this.root = true;
		this.depth = 0;
		this.refMapping = refMapping;
		this.propertyMapping = propertyMapping;
	}

	private DecisionTree(Dragon dtl, ACache sourceCache, ACache targetCache, PseudoFMeasure pseudoFMeasure,
			double minPropertyCoverage, double pruningConfidence, double propertyLearningRate, DecisionTree parent,
			boolean isLeftNode, AMapping refMapping, PropertyMapping propertyMapping) {
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
		if (parent != null) {
			this.depth = this.parent.depth + 1;
		}
		this.refMapping = refMapping;
		this.propertyMapping = propertyMapping;
	}

	public DecisionTree buildTree(int maxDepth) {
		this.classifier = fitnessFunction.getBestClassifier(this);
		if (this.classifier == null) {
			return null;
		}
		if (this.root) {
			totalFMeasure = this.classifier.getfMeasure();
		} else {
			if (fitnessFunction.stopCondition(this)) {
				return null;
			}
		}
		if (maxDepth != this.depth && this.depth < this.propertyMapping.getCompletePropMapping().size()) {
			this.rightChild = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, this, false,
					this.refMapping, this.propertyMapping);
			this.rightChild = this.rightChild.buildTree(maxDepth);
			this.leftChild = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, this, true,
					this.refMapping, this.propertyMapping);
			this.leftChild = this.leftChild.buildTree(maxDepth);
		}
		return this;
	}

	public DecisionTree prune() {
		int currentDepth = maxDepth;
		while (currentDepth >= 0) {
			this.getRootNode().prune(currentDepth);
			currentDepth--;
		}
		return this;
	}

	private DecisionTree prune(int depth) {
		if (this.rightChild == null && this.leftChild == null) {
			return this;
		}
		// Go to the leaves
		if (this.rightChild != null) {
			this.rightChild.prune(depth);
		}
		if (this.leftChild != null) {
			this.leftChild.prune(depth);
		}
		if (this.depth != depth) {
			return this;
		}
		return pruningFunction.pruneChildNodesIfNecessary(this);
	}

	private DecisionTree getRootNode() {
		if (!this.root) {
			if (this.parent == null) {
				logger.error("Detached node!Cannot get root! Returning null!");
				return null;
			}
			return this.parent.getRootNode();
		}
		return this;
	}

	public AMapping getTotalMapping() {
		final DecisionTree rootNode = this.getRootNode();
		final List<String> pathStrings = this.calculatePathMappings(rootNode);
		if (rootNode != null) {
			assert pathStrings.size() > 0;
		}
		AMapping res = MappingFactory.createDefaultMapping();
		final Iterator<String> it = pathMappings.keySet().iterator();
		while (it.hasNext()) {
			final String s = it.next();
			if (!pathStrings.contains(s)) {
				it.remove();
			} else {
				res = MappingOperations.union(pathMappings.get(s), res);
			}
		}
		return res;
	}

	private List<String> calculatePathKeys(DecisionTree node, List<String> pathStrings) {
		if (pathStrings == null) {
			pathStrings = new ArrayList<>();
		}
		if (node.rightChild == null && node.leftChild == null) {
			if (node.root) {
				final String path = node.getPathString();
				if (!pathStrings.contains(path)) {
					pathStrings.add(path);
				}
			} else {
				final String path = node.getPathString();
				if (!pathStrings.contains(path)) {
					pathStrings.add(path);
				}
			}
		} else if (node.rightChild != null && node.leftChild == null) {
			this.calculatePathKeys(node.rightChild, pathStrings);
		} else if (node.leftChild != null && node.rightChild == null) {
			final String path = node.getPathString();
			if (!pathStrings.contains(path)) {
				pathStrings.add(path);
			}
			this.calculatePathKeys(node.leftChild, pathStrings);
		} else {
			this.calculatePathKeys(node.rightChild, pathStrings);
			this.calculatePathKeys(node.leftChild, pathStrings);
		}
		return pathStrings;
	}

	private List<String> calculatePathMappings(DecisionTree node) {
		final List<String> pathStrings = new ArrayList<>();
		if (node.rightChild == null && node.leftChild == null) {
			if (node.root) {
				final AMapping res = node.classifier.getMapping();
				final String path = node.getPathString();
				if (!pathMappings.keySet().contains(path)) {
					pathMappings.put(path, res);
				}
				pathStrings.add(path);
			} else {
				final String path = node.getPathString();
				if (!pathMappings.keySet().contains(path)) {
					pathMappings.put(path, node.getPathMapping());
				}
				pathStrings.add(path);
			}
		} else if (node.rightChild != null && node.leftChild == null) {
			pathStrings.addAll(this.calculatePathMappings(node.rightChild));
		} else if (node.leftChild != null && node.rightChild == null) {
			final String path = node.getPathString();
			if (!pathMappings.keySet().contains(path)) {
				pathMappings.put(path, node.getPathMapping());
			}
			pathStrings.add(path);
			pathStrings.addAll(this.calculatePathMappings(node.leftChild));
		} else {
			pathStrings.addAll(this.calculatePathMappings(node.rightChild));
			pathStrings.addAll(this.calculatePathMappings(node.leftChild));
		}
		return pathStrings;
	}

	public AMapping getPathMapping() {
		if (this.root) {
			return this.classifier.getMapping();
		}
		final AMapping parentMapping = this.parent.getPathMapping();
		final AMapping nodeMapping = this.classifier.getMapping();
		AMapping res = null;
		if (this.isLeftNode) {
			res = MappingOperations.difference(nodeMapping, parentMapping);
		} else {
			res = MappingOperations.intersection(nodeMapping, parentMapping);
		}
		return res;
	}

	public String getPathString() {
		String str = "" + this.depth;
		if (this.root) {
			if (this.classifier.getMetricExpression() != null && !this.classifier.getMetricExpression().equals("")) {
				str += spaceChar + this.classifier.getMetricExpression();
			}
		} else {
			if (this.isLeftNode) {
				str += "left";
			} else {
				str += "right";
			}
			str += this.parent.getPathString() + spaceChar + "³" + this.isLeftNode + "³"
					+ this.classifier.getMetricExpression();
		}
		return str;
	}

	public LinkSpecification getTotalLS() {
		final List<String> paths = this.calculatePathKeys(this.getRootNode(), null);
		final String left = "³true³";
		final String right = "³false³";
		final String[] pathLS = new String[paths.size()];
		int countPathLS = 0;
		for (final String s : paths) {
			double outerThreshold = 0.0;
			final String[] path = s.split(spaceChar);
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
						// Split on | because this an atomic ls and we use the
						// rest as threshold
						if (path.length == 2 && path[i].contains("|")) {
							outerThreshold = Double.parseDouble(path[i].split("\\|")[1]);
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
			final int index = pathLS[0].lastIndexOf("|");
			final String[] lsStringArr = { pathLS[0].substring(0, index), pathLS[0].substring(index + 1) };
			return new LinkSpecification(lsStringArr[0], Double.parseDouble(lsStringArr[1]));
		}
		for (int i = 0; i < pathLS.length; i++) {
			if (i == 0) {
				finalLSString += "OR(" + pathLS[i] + "," + pathLS[i + 1] + ")";
				i++;
			} else {
				finalLSString = "OR(" + finalLSString + "," + pathLS[i] + ")";
			}
			if (i == pathLS.length - 1) {
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
			cloned = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, this.refMapping,
					this.propertyMapping);
			cloned.classifier = new ExtendedClassifier(this.classifier.getMeasure(), this.classifier.getThreshold(),
					this.classifier.getSourceProperty(), this.classifier.getTargetProperty());
			cloned.depth = this.depth;
			if (this.rightChild != null) {
				cloned.rightChild = this.rightChild.cloneWithoutParent();
			}
			if (this.leftChild != null) {
				cloned.leftChild = this.leftChild.cloneWithoutParent();
			}
		} else {
			final DecisionTree parentClone = this.parent.cloneWithoutChild(this.isLeftNode);
			cloned = this.cloneWithoutParent();
			if (this.isLeftNode) {
				parentClone.leftChild = cloned;
				if (this.parent.rightChild != null) {
					parentClone.rightChild = this.parent.rightChild.cloneWithoutParent();
				}
			} else {
				parentClone.rightChild = cloned;
				if (this.parent.leftChild != null) {
					parentClone.leftChild = this.parent.leftChild.cloneWithoutParent();
				}
			}
		}
		return cloned;
	}

	protected DecisionTree cloneWithoutParent() {
		DecisionTree cloned = null;
		if (this.root) {
			cloned = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, this.refMapping,
					this.propertyMapping);
		} else {
			cloned = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, null, this.isLeftNode,
					this.refMapping, this.propertyMapping);
		}
		cloned.classifier = new ExtendedClassifier(this.classifier.getMeasure(), this.classifier.getThreshold(),
				this.classifier.getSourceProperty(), this.classifier.getTargetProperty());
		cloned.depth = this.depth;
		if (this.leftChild != null) {
			final DecisionTree leftClone = this.leftChild.cloneWithoutParent();
			leftClone.parent = cloned;
			cloned.leftChild = leftClone;
		}
		if (this.rightChild != null) {
			final DecisionTree rightClone = this.rightChild.cloneWithoutParent();
			rightClone.parent = cloned;
			cloned.rightChild = rightClone;
		}
		return cloned;
	}

	protected DecisionTree cloneWithoutChild(boolean withoutLeft) {
		DecisionTree cloned = null;
		DecisionTree parentClone = null;
		if (this.parent != null) {
			parentClone = this.parent.cloneWithoutChild(this.isLeftNode);
		}
		if (this.root) {
			cloned = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, this.refMapping,
					this.propertyMapping);
		} else {
			cloned = new DecisionTree(this.dtl, this.sourceCache, this.targetCache, this.pseudoFMeasure,
					this.minPropertyCoverage, this.pruningConfidence, this.propertyLearningRate, parentClone,
					this.isLeftNode, this.refMapping, this.propertyMapping);
		}
		cloned.classifier = new ExtendedClassifier(this.classifier.getMeasure(), this.classifier.getThreshold());
		cloned.depth = this.depth;
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
		final GoldStandard gs = new GoldStandard(refMap, this.dtl.getTestSourceCache().getAllUris(),
				this.dtl.getTestTargetCache().getAllUris());
		final FMeasure fm = new FMeasure();
		res = fm.calculate(mapping, gs);
		return res;
	}

	public AMapping getMeasureMapping(String measureExpression, ExtendedClassifier cp) {
		if (this.root) {
			final AMapping mapping = this.executeAtomicMeasure(measureExpression, cp.getThreshold());
			calculatedMappings.put(cp.getMetricExpression(), mapping);
			return mapping;
		}
		this.classifier = cp;
		AMapping classifierMapping = calculatedMappings.get(cp.getMetricExpression());
		if (classifierMapping == null) {
			classifierMapping = this.executeAtomicMeasure(measureExpression, cp.getThreshold());
			calculatedMappings.put(cp.getMetricExpression(), classifierMapping);
		}
		this.classifier.setMapping(classifierMapping);
		return this.getTotalMapping();
	}

	public AMapping executeAtomicMeasure(String measureExpression, double threshold) {
		final Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1,
				-1);
		final ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, this.sourceCache,
				this.targetCache, "?x", "?y", 0, 1.0);
		final Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	public String toStringOneLine() {
		String res = "";
		if (this.classifier != null) {
			res += this.classifier.getMeasure() + delimiter + this.classifier.getSourceProperty() + "|"
					+ this.classifier.getTargetProperty() + ": <= " + this.classifier.getThreshold() + ", > "
					+ this.classifier.getThreshold() + "[";
			if (this.leftChild != null) {
				res += this.leftChild.toString();
			} else {
				res += "negative (0)";
			}
			res += "][";
			if (this.rightChild != null) {
				res += this.rightChild.toString();
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
		res += new String(new char[this.depth]).replace("\0", "\t");
		if (this.classifier != null) {
			res += this.classifier.getMeasure() + delimiter + this.classifier.getSourceProperty() + "|"
					+ this.classifier.getTargetProperty() + ": <= " + this.classifier.getThreshold() + ", > "
					+ this.classifier.getThreshold() + "[";
			if (this.leftChild != null) {
				res += this.leftChild.toString();
			} else {
				res += "negative (0)";
			}
			res += "][";
			if (this.rightChild != null) {
				res += this.rightChild.toString();
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
		return this.refMapping;
	}

	public double getMinPropertyCoverage() {
		return this.minPropertyCoverage;
	}

	public double getPropertyLearningRate() {
		return this.propertyLearningRate;
	}

	public ACache getSourceCache() {
		return this.sourceCache;
	}

	public ACache getTargetCache() {
		return this.targetCache;
	}

	public ExtendedClassifier getClassifier() {
		return this.classifier;
	}

	public void setClassifier(ExtendedClassifier classifier) {
		this.classifier = classifier;
	}

	public DecisionTree getParent() {
		return this.parent;
	}

	public Dragon getDtl() {
		return this.dtl;
	}

	public boolean isLeftNode() {
		return this.isLeftNode;
	}

	public boolean isRoot() {
		return this.root;
	}

	public DecisionTree getLeftChild() {
		return this.leftChild;
	}

	public void setLeftChild(DecisionTree leftChild) {
		this.leftChild = leftChild;
	}

	public DecisionTree getRightChild() {
		return this.rightChild;
	}

	public void setRightChild(DecisionTree rightChild) {
		this.rightChild = rightChild;
	}

	public void setParent(DecisionTree parent) {
		this.parent = parent;
	}

	public double getPruningConfidence() {
		return this.pruningConfidence;
	}

	public void setPruningConfidence(double pruningConfidence) {
		this.pruningConfidence = pruningConfidence;
	}

	public PropertyMapping getPropertyMapping() {
		return this.propertyMapping;
	}

	public void setPropertyMapping(PropertyMapping propertyMapping) {
		this.propertyMapping = propertyMapping;
	}

}
