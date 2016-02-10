package org.aksw.limes.core.ml.algorithm.lion;

import java.util.List;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.apache.log4j.Logger;


/**
 * Search tree node heuristic, which uses the score (e.g. pseudo F-measure) of a spec
 * and its size.
 * 
 * @author Jens Lehmann
 *
 */
public class DefaultRefinementHeuristic implements RefinementHeuristic {
	static Logger logger = Logger.getLogger("LIMES");
	/*
	 * Evaluation: check influence of expansion and bonus
	 */
	// penalty for long link specs
	public double expansionPenaltyFactor = 0.1;	
	public double bonus = 0.1;	//what best value to assign ??
	LearningSetting setting;
	
//	@Override
	public int compare(SearchTreeNode node1, SearchTreeNode node2) {
		double diff = getNodeScore(node1) - getNodeScore(node2);
		
		if(diff>0) {		
			return 1;
		} else if(diff<0) {
			return -1;
		} else {//Or use the expansion value where the smaller one means more opportunity to be visited again giving more refinement tracks
			if(node1.getExpansion() != node2.getExpansion()) {
				if(node1.getExpansion() < node2.getExpansion())
					return 1;
				else
					return -1;
			} else 
			if(node1.getSpec().size() != node2.getSpec().size()) {
				if(node1.getSpec().size() < node2.getSpec().size())
					return 1;
				return -1;
			} else
			{
			logger.info("compare node1, node2: same score, same expansion, same linkspec size. Using String comparison");
	
			// TODO: ugly hack - a nicer comparator for link spec should be used here
			/*String firstNodeExpression = node1.getSpec().getMeasure();
			String secondNodeExpression = node2.getSpec().getMeasure();*/
			// Here to compare two specs i assume that the spec with more expressions in its metric
			// provides more chances for different results when applying the refinement operator on its metric 
			List<LinkSpecification> firstAllLeaves = node1.getSpec().getAllLeaves();
			List<LinkSpecification> secondAllLeaves = node1.getSpec().getAllLeaves();
			if(firstAllLeaves.size() >= secondAllLeaves.size())
				return 1;
			else
				return -1;
			}
		}		
	}
	
	public double getNodeScore(SearchTreeNode node) {
		// baseline score
		double score = node.getScore();
		
		// TODO other idea: give a bonus if the node has a better score than its parent
		
		double parentScore =  node.getParent().getScore();
		if(score >  parentScore)
			score += node.getExpansion() * bonus;
		// penalty for horizontal expansion
		
		score -= node.getExpansion() * expansionPenaltyFactor;
		
		return score;
	}

	@Override
	public void setLearningSetting(LearningSetting setting) {
		this.setting = setting;
	}
}
