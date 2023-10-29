/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
