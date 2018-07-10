package org.aksw.limes.core.measures.measure.customGraphs.relabling;

import java.util.function.Consumer;

/**
 * Interface of Labels: the Collector only for Source Literals
 * @author Cedric Richter
 */
public interface ILabelCollector {

    public Consumer<ILabel> getSourceLabelConsumer();

}
