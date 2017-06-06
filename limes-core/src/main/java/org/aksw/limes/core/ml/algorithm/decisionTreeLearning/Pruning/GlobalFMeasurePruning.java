package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning;

import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTree;

public class GlobalFMeasurePruning extends PruningFunctionDTL{

		@Override
		public DecisionTree pruneChildNodesIfNecessary(DecisionTree node) {
            DecisionTree tmpRightChild = node.getRightChild();
            DecisionTree tmpLeftChild = node.getLeftChild();
            boolean deleteLeft = false;
            boolean deleteRight = false;
            node.setRightChild(null);
            double tmp = 0.0;
            tmp = node.calculateFMeasure(node.getTotalMapping(), node.getRefMapping());
            if (tmp >= DecisionTree.totalFMeasure) {
                deleteRight = true;
            }
            node.setRightChild(tmpRightChild);
            node.setLeftChild(null);
            tmp = node.calculateFMeasure(node.getTotalMapping(), node.getRefMapping());
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteLeft = true;
                deleteRight = false;
            }
            node.setRightChild(null);
            node.setLeftChild(null);
            tmp = node.calculateFMeasure(node.getTotalMapping(), node.getRefMapping());
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteLeft = true;
                deleteRight = true;
            }
            if (!deleteLeft) {
                node.setLeftChild(tmpLeftChild);
            }
            if (!deleteRight) {
                node.setRightChild(tmpRightChild);
            }
			return node;
		}
}
