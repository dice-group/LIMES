package org.aksw.limes.core.measures.measure.customGraphs.relabling;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.description.IEdge;
import org.aksw.limes.core.measures.measure.customGraphs.description.INode;
import org.aksw.limes.core.measures.measure.customGraphs.description.impl.BaseNode;

import java.util.*;

/**
 * Relabeling Wrapper automatically changes edge types and literals.
 *
 * Wrapper for relabeled edges, gathers every relabeled node, source, target etc. and add them to graphView
 *
 * @author Cedric Richter
 */
public class RelablingWrapper implements IDescriptionGraphView {

    private IDescriptionGraphView delegate;
    private IGraphRelabel relabel;
    private Map<ILabel, String> relabelCache = new HashMap<>();

    public RelablingWrapper(IDescriptionGraphView delegate, IGraphRelabel relabel) {
        this.delegate = delegate;
        this.relabel = relabel;
    }

    @Override
    public String getRoot() {
        return delegate.getRoot();
    }

    private Map<ILabel, String> relabel(Set<ILabel> labels){

        Map<ILabel, String> relabelMapping = new HashMap<>();

        Set<ILabel> search = new HashSet<>();

        for(ILabel label: labels){
            if(relabelCache.containsKey(label)){
                relabelMapping.put(label, relabelCache.get(label));
            }else{
                search.add(label);
            }
        }

        if(!search.isEmpty()) {
            Map<ILabel, String> searches = relabel.relabel(search);
            relabelMapping.putAll(searches);
            relabelCache.putAll(searches);
        }

        return relabelMapping;
    }

    private String fixEmptyString(String s){
        if(s.isEmpty()){
            return "EMPTY";
        }
        return s;
    }

    @Override
    public Set<INode> getNodes() {
       return delegate.getNodes();
    }

    @Override
    public Set<INode> getNodesAndLeaves() {
        Set<INode> out = new HashSet<>();

        Set<ILabel> openLabels = new HashSet<>();

        for(INode node: delegate.getNodesAndLeaves()){
            if(node.getType() == INode.NodeType.URL){
                out.add(node);
                continue;
            }
            openLabels.add(new Label(ILabel.LabelType.NODE, fixEmptyString(node.getLabel())));
        }

        Map<ILabel, String> relabel = relabel(openLabels);

        for(Map.Entry<ILabel, String> e: relabel.entrySet()){
            if(e.getValue() != null){
                out.add(new BaseNode(e.getValue(), INode.NodeType.LITERAL));
            }
        }

        return out;
    }

    @Override
    public Multimap<String, IEdge> getNeighbours(INode node) {
        Multimap<String, IEdge> map = delegate.getNeighbours(node);
        Multimap<String, IEdge> out = HashMultimap.create();

        Set<ILabel> openLabels = new HashSet<>();

        for(IEdge edge: map.values()){
            openLabels.add(new Label(ILabel.LabelType.EDGE, edge.getEdgeType()));
            if(edge.getTarget().getType() == INode.NodeType.LITERAL)
                openLabels.add(new Label(ILabel.LabelType.NODE, fixEmptyString(edge.getTarget().getLabel())));
        }

        Map<ILabel, String> relabel = relabel(openLabels);


        for(IEdge edge: map.values()){
            String edgeType = relabel.get(new Label(ILabel.LabelType.EDGE, edge.getEdgeType()));

            String objectLabel = edge.getTarget().getLabel();
            if(edge.getTarget().getType() != INode.NodeType.URL) {
                objectLabel = relabel.get(new Label(ILabel.LabelType.NODE, edge.getTarget().getLabel()));
                if(objectLabel==null)continue;
            }

            out.put(edgeType, new Edge(edge.getSource(),
                                       edgeType,
                                       new BaseNode(objectLabel,
                                                    edge.getTarget().getType())
                                       ));
        }

        return out;
    }

    private class Label implements ILabel{

        private LabelType type;
        private String content;

        public Label(LabelType type, String content) {
            this.type = type;
            this.content = content;
        }

        @Override
        public LabelType getType() {
            return type;
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Label label = (Label) o;
            return type == label.type &&
                    Objects.equals(content, label.content);
        }

        @Override
        public int hashCode() {

            return Objects.hash(type, content);
        }
    }

    private class Edge implements IEdge{

        private INode source;
        private String edgeType;
        private INode target;

        public Edge(INode source, String edgeType, INode target) {
            this.source = source;
            this.edgeType = edgeType;
            this.target = target;
        }

        @Override
        public INode getSource() {
            return source;
        }


        @Override
        public String getEdgeType() {
            return edgeType;
        }

        @Override
        public INode getTarget() {
            return target;
        }
    }
}
