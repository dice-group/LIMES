package org.aksw.limes.core.measures.measure.customGraphs.description;

/**
 * @author Cedric Richter
 */
public interface IEdge {

    public INode getSource();

    public String getEdgeType();

    public INode getTarget();

}
