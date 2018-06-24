package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import com.google.common.collect.Multimap;
import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.aksw.limes.core.measures.measure.customGraphs.description.IEdge;
import org.aksw.limes.core.measures.measure.customGraphs.description.INode;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;
import org.apache.jena.rdf.model.RDFNode;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public class ListeningDescriptionGraphView implements IGraphLoaded {

    private IGraphLoaded delegate;
    private List<Consumer<ILabel>> listener;

    ListeningDescriptionGraphView(IGraphLoaded delegate, List<Consumer<ILabel>> listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    private String fixEmptyString(String s){
        if(s.isEmpty()){
            return "EMPTY";
        }
        return s;
    }

    @Override
    public void onStatement(String src, String property, RDFNode object) {

        for(Consumer<ILabel> consumer: listener) {
            //consumer.accept(new Label(ILabel.LabelType.EDGE, property));
            if(object.isLiteral())
                consumer.accept(new Label(ILabel.LabelType.NODE, fixEmptyString(object.toString())));
        }

        delegate.onStatement(src, property, object);
    }

    @Override
    public String getRoot() {
        return delegate.getRoot();
    }

    @Override
    public Set<INode> getNodes() {
        return delegate.getNodes();
    }

    @Override
    public Set<INode> getNodesAndLeaves() {
        return delegate.getNodesAndLeaves();
    }

    @Override
    public Multimap<String, IEdge> getNeighbours(INode node) {
        return delegate.getNeighbours(node);
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
    }
}
