package org.aksw.limes.core.ml.algorithm.lion;

import java.util.Comparator;

import org.aksw.limes.core.ml.setting.LearningSetting;

/**
 * Search algorithm heuristic for the refinement based algorithm.
 * 
 * @author Jens Lehmann
 * @author Klaus Lyko
 *
 */
public interface RefinementHeuristic extends Comparator<SearchTreeNode> {

	void setLearningSetting(LearningSetting setting);
	
}
