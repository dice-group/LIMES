package org.aksw.limes.core.measures.measure.customGraphs.relabling;

/**
 * Interface of Labels, gives the type and content of the label
 * @author Cedric Richter
 */
public interface ILabel {

    enum LabelType{
        NODE, LEAF, EDGE
    }

    public LabelType getType();

    public String getContent();

}
