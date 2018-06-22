package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CollectingGrouperWrapper implements ICollectingNodeLabelGrouper{

    private INodeLabelGrouper delegate;
    private Consumer<String> nodeLabelConsumer;

    public CollectingGrouperWrapper(INodeLabelGrouper delegate) {
        this.delegate = delegate;
    }

    @Override
    public String group(Node n) {
        if(n instanceof Node_Literal){
            consume(n.getLiteral().toString());
        }
        return this.delegate.group(n);
    }

    private void consume(String s){
        if(nodeLabelConsumer != null)
            nodeLabelConsumer.accept(s);
    }

    @Override
    public void endGrouping() {
        this.delegate.endGrouping();
    }


    @Override
    public void collectOn(Consumer<String> consumer) {
        this.nodeLabelConsumer = consumer;
    }
}
