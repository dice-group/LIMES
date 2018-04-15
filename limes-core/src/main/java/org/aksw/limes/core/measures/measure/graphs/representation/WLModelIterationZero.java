package org.aksw.limes.core.measures.measure.graphs.representation;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.aksw.limes.core.measures.measure.graphs.gouping.INodeLabelGrouper;
import org.apache.jena.rdf.model.Model;

import java.util.Map;
import java.util.Set;

public class WLModelIterationZero implements WLModelIteration {

    private GraphModelRepresentation representation;
    private Multiset<String> set;

    public WLModelIterationZero(GraphModelRepresentation representation) {
        this.representation = representation;
    }

    private Multiset<String> initSet(){
        set = HashMultiset.create();
        Set<String> vertices = representation.getVertices();
        for(String v: vertices){
            for(GraphModelRepresentation.Edge e: representation.getNeighbours(v)){
                String obj = e.getNode();
                if(!vertices.contains(obj)){
                    set.add(obj);
                }
            }
        }
        this.representation = null;
        return set;
    }

    @Override
    public Multiset<String> getRepresentation() {
        if(set == null){
            return initSet();
        }else{
            return set;
        }
    }


}
