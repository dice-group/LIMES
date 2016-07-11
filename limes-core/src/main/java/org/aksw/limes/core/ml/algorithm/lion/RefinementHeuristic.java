package org.aksw.limes.core.ml.algorithm.lion;

import org.aksw.limes.core.ml.setting.LearningParameter;
import org.aksw.limes.core.ml.setting.LearningSetting;

import java.util.Comparator;
import java.util.List;

/**
 * Search algorithm heuristic for the refinement based algorithm.
 *
 * @author Jens Lehmann
 * @author Klaus Lyko
 */
public interface RefinementHeuristic extends Comparator<SearchTreeNode> {

	@Deprecated
    void setLearningSetting(LearningSetting setting);
    
    void setLearningParameters(List<LearningParameter> par);

}
