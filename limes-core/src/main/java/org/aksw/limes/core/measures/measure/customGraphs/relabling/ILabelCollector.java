package org.aksw.limes.core.measures.measure.customGraphs.relabling;

import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public interface ILabelCollector {

    public Consumer<ILabel> getSourceLabelConsumer();

}
