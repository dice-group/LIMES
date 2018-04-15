package org.aksw.limes.core.io.describe;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.utils.CannedQueryUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Model;


public class ResourceDescriptor implements IResourceDescriptor {

    private QueryExecutionFactory qef;
    private Node node;

    ResourceDescriptor(QueryExecutionFactory factory, Node node){
        this.qef = factory;
        this.node = node;
    }

    @Override
    public String getURI() {
        return node.getURI();
    }

    @Override
    public Model queryDescription() {
        QueryExecution qe = qef.createQueryExecution(
                CannedQueryUtils.describe(this.node)
        );
        return qe.execDescribe();
    }
}
