package org.aksw.limes.core.measures.measure.customGraphs.relabling;

import java.util.Map;
import java.util.Set;

/**
 * Interface that provides that relabeled graph
 * @author Cedric Richter
 */
public interface IGraphRelabel {

    /**
     * Expected to return:
     *  - null : No prior knowledge is required
     *  - ILabelCollector :  prior source knowledge is required
     *  - ITwoSideLabelCollector : full prior knowledge is required
     * @return prior knowledge consumer
     */
    public ILabelCollector getPriorLabelCollector();

    /**
     * Relabels the current label
     * @param label the current label
     * @return the changed label or null if the label should be ignored
     */
    public String relabel(ILabel label);

    public Map<ILabel, String> relabel(Set<ILabel> labels);
}
