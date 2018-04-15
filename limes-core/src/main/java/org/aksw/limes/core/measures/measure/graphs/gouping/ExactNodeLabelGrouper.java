package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;

public class ExactNodeLabelGrouper implements INodeLabelGrouper{
    @Override
    public String group(Node n) {
        if(n instanceof Node_URI){
            return n.getURI();
        }
        if(n instanceof Node_Literal){
            return n.getLiteral().toString();
        }
        return n.toString();
    }
}
