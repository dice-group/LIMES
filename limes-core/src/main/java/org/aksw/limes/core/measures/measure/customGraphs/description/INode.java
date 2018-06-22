package org.aksw.limes.core.measures.measure.customGraphs.description;


/**
 * @author Cedric Richter
 */
public interface INode {

    public enum NodeType{
        URL, LITERAL
    }

    public String getLabel();

    public NodeType getType();

}
