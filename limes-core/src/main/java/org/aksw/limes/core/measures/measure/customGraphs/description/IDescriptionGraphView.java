package org.aksw.limes.core.measures.measure.customGraphs.description;


import com.google.common.collect.Multimap;


import java.util.Set;

/**
 * @author Cedric Richter
 */
public interface IDescriptionGraphView {

    public String getRoot();

    public Set<INode> getNodes();

    public Set<INode> getNodesAndLeaves();

    public Multimap<String, IEdge> getNeighbours(INode node);

}
