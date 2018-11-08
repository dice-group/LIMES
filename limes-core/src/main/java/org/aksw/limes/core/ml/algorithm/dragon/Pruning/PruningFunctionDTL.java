package org.aksw.limes.core.ml.algorithm.dragon.Pruning;

import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;

public abstract class PruningFunctionDTL {

	/**
	 * Decides if all the child nodes or one of them have to be pruned
	 * The nodes have to have the appropriate mappings in the classifiers
	 * The child nodes of this node are leaves
	 * @param node
	 * @return node
	 */
	public abstract DecisionTree pruneChildNodesIfNecessary(DecisionTree node);
}
