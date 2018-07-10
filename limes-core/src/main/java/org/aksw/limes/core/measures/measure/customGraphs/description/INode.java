package org.aksw.limes.core.measures.measure.customGraphs.description;


/**
 * Interface to get label and type of a current node
 *
 * @author Cedric Richter
 */
public interface INode {

    public enum NodeType{
        URL, LITERAL
    }

    public String getLabel();

    public NodeType getType();

}
