package org.aksw.limes.core.ml.algorithm.dragon.Pruning;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalFMeasurePruning extends PruningFunctionDTL{
	protected static Logger logger = LoggerFactory.getLogger(GlobalFMeasurePruning.class);

		@Override
		public DecisionTree pruneChildNodesIfNecessary(DecisionTree node) {
            DecisionTree tmpRightChild = node.getRightChild();
            DecisionTree tmpLeftChild = node.getLeftChild();
            boolean deleteLeft = false;
            boolean deleteRight = false;
            boolean leftalreadynull = false;
            boolean rightalreadynull = false;
            if(tmpLeftChild == null){
            	leftalreadynull = true;
            }
            if(tmpRightChild == null){
            	rightalreadynull = true;
            }
            
            node.setRightChild(null);
            double tmp = 0.0;
            AMapping withoutRight = node.getTotalMapping();
            tmp = node.calculateFMeasure(withoutRight, node.getRefMapping());
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteRight = true;
            }
            node.setRightChild(tmpRightChild);
            node.setLeftChild(null);
            AMapping withoutLeft = node.getTotalMapping();
            tmp = node.calculateFMeasure(withoutLeft, node.getRefMapping());
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteLeft = true;
                deleteRight = false;
            }
            node.setRightChild(null);
            node.setLeftChild(null);
            AMapping withoutBoth = node.getTotalMapping();
            tmp = node.calculateFMeasure(withoutBoth, node.getRefMapping());
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteLeft = true;
                deleteRight = true;
            }
            
            if(leftalreadynull){
            	assert withoutRight.equals(withoutBoth);
            }
            if(rightalreadynull){
            	assert withoutLeft.equals(withoutBoth);
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
