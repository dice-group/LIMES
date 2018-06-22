package org.aksw.limes.core.measures.measure.customGraphs.relabling;

import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public interface ITwoSideLabelCollector extends ILabelCollector {

    public Consumer<ILabel> getTargetLabelConsumer();

}
