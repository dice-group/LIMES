package org.aksw.limes.core.ml.algorithm.lion.graphic;

import org.aksw.limes.core.ml.algorithm.lion.SearchTreeNode;

public class NodeHistory {
	String expression="";
	int expansion;
	double score;
	double bestScore;
	double threshold;
	int children;
	public int historyTiming; // this is to record the timing for the history event
	public NodeHistory(SearchTreeNode node)
	{
		expression= node.getSpec().getFilterExpression();
		score = node.getScore();
		bestScore = node.getBestScore();
		threshold = node.getSpec().getThreshold();
		expansion = node.getExpansion();
		children = node.getChildren().size();
	}
	
	@Override
	public String toString() {
		
		return super.toString();
	}

}
