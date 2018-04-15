package org.aksw.limes.core.measures.measure.graphs.representation;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WLModelIterationNext implements WLModelIteration {

    private GraphModelRepresentation representation;
    private Multiset<String> set;
    private Map<String, String> mapping;
    private WLModelIteration previous;
    private HashFunction function;

    public WLModelIterationNext(GraphModelRepresentation representation, WLModelIteration previous) {
        this.representation = representation;
        this.previous = previous;
        this.function = Hashing.murmur3_128();
    }

    private String relabel(String s){
        if(mapping == null)initSet();
        if(mapping.containsKey(s)){
            return mapping.get(s);
        }
        return s;
    }

    private String previousRelabel(String s){
        if(previous instanceof WLModelIterationNext){
            return ((WLModelIterationNext) previous).relabel(s);
        }
        return s;
    }

    private String calcNewLabel(String s){
        String label = "";

        String relabel = previousRelabel(s);
        if(!relabel.equals(s)){
            label += relabel;
        }

        for(GraphModelRepresentation.Edge e: representation.getNeighbours(s)){
            label += this.function.hashString(e.getEdgeName()+" "+previousRelabel(e.getNode()),
                                                Charset.defaultCharset())
                        .toString() + " ";
        }

        return this.function.hashString(label, Charset.defaultCharset()).toString();
    }



    private Multiset<String> initSet(){
        set = HashMultiset.create();
        mapping = new HashMap<>();

        Set<String> vertices = representation.getVertices();
        for(String v: vertices){
            mapping.put(v, calcNewLabel(v));
        }

        for(String v: vertices){
            set.add(this.relabel(v));
            for(GraphModelRepresentation.Edge e: representation.getNeighbours(v)){
                String obj = e.getNode();
                if(!vertices.contains(obj)){
                    set.add(this.relabel(obj));
                }
            }
        }

        this.representation = null;
        this.previous = null;
        return set;
    }



    @Override
    public Multiset<String> getRepresentation() {
        if(set == null){
            return initSet();
        }
        return set;
    }
}
