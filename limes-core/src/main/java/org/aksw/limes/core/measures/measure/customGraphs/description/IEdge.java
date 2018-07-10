package org.aksw.limes.core.measures.measure.customGraphs.description;

/**
 * Interface that provides source, type of edge and target edge to be relabeled.
 *
 * @author Cedric Richter
 */
public interface IEdge {

    public INode getSource();

    public String getEdgeType();

    public INode getTarget();

}
