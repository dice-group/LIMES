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

    /**
     * Creates a lazy descriptor without support for recursive expansion
     * @param uri IRI of the resource to query
     * @return a lazy resource descriptor
     */
    public IResourceDescriptor createDescriptor(String uri){
        return this.createDescriptor(nodeFactory.createURI(uri));
    }

    /**
     * Creates a lazy descriptor without support for recursive expansion
     * @param node Jena model node representing a resource to query
     * @return a lazy resource descriptor
     */
    public IResourceDescriptor createDescriptor(Node node){
        return new ResourceDescriptor(this.factory, node);
    }

    /**
     * Creates a lazy descriptor with support for recursive expansion
     * @param node Jena model node representing a resource to query
     * @param depth recursion depth
     * @return a lazy resource descriptor
     */
    public IResourceDescriptor createRecursiveDescriptor(int depth, Node node){
        return new RecursiveResourceDescriptor(depth, this.factory, node);
    }

    /**
     * Creates a lazy descriptor without support for recursive expansion
     * @param uri IRI of the resource to query
     * @param depth recursion depth
     * @return a lazy resource descriptor
     */
    public IResourceDescriptor createRecursiveDescriptor(int depth, String uri){
        return new RecursiveResourceDescriptor(depth, this.factory, nodeFactory.createURI(uri));
    }
}
