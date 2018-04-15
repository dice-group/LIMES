package org.aksw.limes.core.io.describe;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

public class ResourceDescriptorFactory {

    private QueryExecutionFactory factory;
    private NodeFactory nodeFactory;

    ResourceDescriptorFactory(QueryExecutionFactory qef){
        this.factory = qef;
        this.nodeFactory = new NodeFactory();
    }

    public IResourceDescriptor createDescriptor(String uri){
        return this.createDescriptor(nodeFactory.createURI(uri));
    }

    public IResourceDescriptor createDescriptor(Node node){
        return new ResourceDescriptor(this.factory, node);
    }

}
