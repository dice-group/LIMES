package org.aksw.limes.core.measures.measure.customGraphs.relabling;

/**
 * @author Cedric Richter
 */
public interface ILabel {

    enum LabelType{
        NODE, LEAF, EDGE
    }

    public LabelType getType();

    public String getContent();

}
