package org.aksw.limes.core.measures.measure.graphs.representation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.aksw.limes.core.measures.measure.graphs.gouping.IEdgeLabelGrouper;
import org.aksw.limes.core.measures.measure.graphs.gouping.INodeLabelGrouper;
import org.aksw.limes.core.measures.measure.graphs.gouping.NulifyEdgeLabelGrouper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.Collection;
import java.util.Set;


public class GraphModelRepresentation {

    private INodeLabelGrouper nodeLabelGrouper;
    private IEdgeLabelGrouper edgeLabelGrouper;
    private IResourceDescriptor descriptor;
    private Multimap<String, Edge> map;


    public GraphModelRepresentation(IResourceDescriptor descriptor, INodeLabelGrouper grouper, IEdgeLabelGrouper edgeGrouper){
        this.descriptor = descriptor;
        this.nodeLabelGrouper = grouper;
        this.edgeLabelGrouper = edgeGrouper;
    }

    public GraphModelRepresentation(IResourceDescriptor descriptor, INodeLabelGrouper grouper){
        this(descriptor, grouper, new NulifyEdgeLabelGrouper());
    }

    private void lazyLoad(){
        if(map != null)return;
        map = HashMultimap.create();
        StmtIterator it = descriptor.queryDescription().listStatements();

        while(it.hasNext())
            parseStatement(it.nextStatement(), nodeLabelGrouper, edgeLabelGrouper);
    }

    private void parseStatement(Statement statement, INodeLabelGrouper grouper, IEdgeLabelGrouper edgeGrouper){
        String src = grouper.group(statement.getSubject().asNode());
        String property = edgeGrouper.group(statement.getPredicate().asNode());
        String target = grouper.group(statement.getObject().asNode());

        map.put(src, new Edge(property, target));

    }

    public IResourceDescriptor getRootDescriptor(){
        return this.descriptor;
    }

    public Set<String> getVertices(){
        lazyLoad();
        return map.keySet();
    }

    public Collection<Edge> getNeighbours(String s){
        lazyLoad();
        return map.get(s);
    }

    public class Edge{

        private String edgeName;
        private String node;

        public Edge(String edgeName, String node) {
            if(edgeName == null)edgeName = "";
            this.edgeName = edgeName;
            this.node = node;
        }

        public String getEdgeName() {
            return edgeName;
        }

        public String getNode() {
            return node;
        }

    }

}
