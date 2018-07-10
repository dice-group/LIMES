package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.aksw.limes.core.measures.measure.customGraphs.description.IEdge;
import org.aksw.limes.core.measures.measure.customGraphs.description.INode;
import org.apache.jena.rdf.model.RDFNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Quick description of graph via loading hook and multimap
 * i.e. from Literal to URI
 *
 * @author Cedric Richter
 */
public class EagerDescriptionGraphView implements IGraphLoaded {

    private LoadingHook loader;
    private Map<INode, Multimap<String, IEdge>> graph = new HashMap<>();

    EagerDescriptionGraphView(LoadingHook loader) {
        this.loader = loader;
        this.loader.load();
    }

    @Override
    public void onStatement(String src, String property, RDFNode object) {

        INode srcNode = new BaseNode(src, INode.NodeType.URL);

        INode targetNode;
        if(object.isLiteral())
            targetNode = new BaseNode(object.toString(), INode.NodeType.LITERAL);
        else
            targetNode = new BaseNode(object.toString(), INode.NodeType.URL);


        graph.putIfAbsent(srcNode, HashMultimap.create());
        graph.get(srcNode).put(property, new BaseEdgeContainer(srcNode, property, targetNode));
    }



    @Override
    public String getRoot() {
        return loader.getDescriptor().getURI();
    }

    @Override
    public Set<INode> getNodes() {
        return graph.keySet();
    }

    @Override
    public Set<INode> getNodesAndLeaves() {
        Set<INode> out = new HashSet<>(getNodes());

        for(Multimap<String, IEdge> map: graph.values()){
            for(IEdge e: map.values()){
                out.add(e.getTarget());
            }
        }

        return out;
    }

    @Override
    public Multimap<String, IEdge> getNeighbours(INode node) {
        return graph.getOrDefault(node, HashMultimap.create());
    }
}
