package org.aksw.limes.core.io.describe;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.utils.CannedQueryUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.*;


import java.util.Stack;

public class RecursiveModelBuilder {

    private QueryExecutionFactory qef;
    private Node node;

    RecursiveModelBuilder(QueryExecutionFactory factory, Node node){
        this.qef = factory;
        this.node = node;
    }

    private Model describe(Node n){
        QueryExecution qe = qef.createQueryExecution(
                CannedQueryUtils.outgoing(n)
        );
        return qe.execDescribe();
    }

    private Model concat(Model m1, Model m2){
        if(m1 == null)
            return m2;
        if(m2 == null)
            return m1;
        return m1.add(m2);
    }

    public Node getNode() {
        return node;
    }

    public Model build(int depth){
        Model out = null;

        Stack<DescriptionNode> stack = new Stack<>();
        stack.push(new DescriptionNode(this.node, 0));

        while(!stack.isEmpty()){
            DescriptionNode n = stack.pop();

            if(n.depth > depth)
                continue;

            Model m = describe(n.node);

            if(n.depth + 1 <= depth) {

                NodeIterator iterator = m.listObjects();

                while (iterator.hasNext()) {
                    RDFNode neigh = iterator.next();

                    if (neigh.isResource()) {
                        stack.push(new DescriptionNode(neigh.asNode(), n.depth+1));
                    }

                }
            }

            out = concat(out, m);

        }

        return out;
    }


    private class DescriptionNode{

        Node node;
        int depth;

        public DescriptionNode(Node node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

}
