package org.aksw.limes.core.ml.algorithm.lion;

import org.aksw.limes.core.ml.setting.LearningSetting;

import java.util.Comparator;

/**
 * Search algorithm heuristic for the refinement based algorithm.
 *
 * @author Jens Lehmann
 * @author Klaus Lyko
 */
public interface RefinementHeuristic extends Comparator<SearchTreeNode> {

    void setLearningSetting(LearningSetting setting);

}
