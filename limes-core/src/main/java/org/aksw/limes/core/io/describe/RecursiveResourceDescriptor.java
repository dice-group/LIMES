package org.aksw.limes.core.io.describe;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;

public class RecursiveResourceDescriptor implements IResourceDescriptor {

    private int depth;
    private RecursiveModelBuilder builder;

    RecursiveResourceDescriptor(int depth, QueryExecutionFactory factory, Node node){
        this.builder = new RecursiveModelBuilder(factory, node);
        this.depth = depth;
    }

    RecursiveResourceDescriptor(QueryExecutionFactory factory, Node node){
        this(2, factory, node);
    }

    @Override
    public String getURI() {
        return this.builder.getNode().getURI();
    }

    @Override
    public Model queryDescription() {
        return builder.build(depth);
    }
}
