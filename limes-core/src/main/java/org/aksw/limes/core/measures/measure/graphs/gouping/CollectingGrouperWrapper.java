package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;

import java.util.HashSet;
import java.util.Set;

public class CollectingGrouperWrapper implements INodeLabelGrouper {

    private INodeLabelGrouper delegate;
    private Set<String> nodeLabels = new HashSet<>();

    public CollectingGrouperWrapper(INodeLabelGrouper delegate) {
        this.delegate = delegate;
    }

    @Override
    public String group(Node n) {
        if(n instanceof Node_Literal){
            this.nodeLabels.add(n.getLiteral().toString());
        }
        return this.delegate.group(n);
    }

    @Override
    public void endGrouping() {
        this.delegate.endGrouping();
    }

    public Set<String> getCollectedLabels(){
        return this.nodeLabels;
    }
}
