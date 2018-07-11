package org.aksw.limes.core.measures.measure.customGraphs.relabling;

import java.util.function.Consumer;


/**
 * Interface of Labels: the Collector only for Target Literals/Labels on both sides
 * @author Cedric Richter
 */
public interface ITwoSideLabelCollector extends ILabelCollector {

    public Consumer<ILabel> getTargetLabelConsumer();

}
