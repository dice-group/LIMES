package org.aksw.limes.core.measures.measure.graphs.representation;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.*;

public class WLModelIterationNext implements WLModelIteration {

    private GraphModelRepresentation representation;
    private Multiset<String> set;
    private Map<String, String> nodeMapping;
    private WLModelIteration previous;
    private HashFunction function;

    public WLModelIterationNext(GraphModelRepresentation representation, WLModelIteration previous) {
        this.representation = representation;
        this.previous = previous;
        this.function = Hashing.murmur3_128();
    }

    private String relabelNode(String s){
        if(nodeMapping == null)initSet();
        if(nodeMapping.containsKey(s)){
            return nodeMapping.get(s);
        }
        return s;
    }

    private String previousRelabelNode(String s){
        if(previous instanceof WLModelIterationNext){
            return ((WLModelIterationNext) previous).relabelNode(s);
        }
        return s;
    }

    private String calcNewLabel(String s){
        String label = "";

        String relabel = previousRelabelNode(s);
        if(!relabel.equals(s)){
            label += relabel;
        }

        List<String> neighbourhoodLabels = new ArrayList<>();
        for(GraphModelRepresentation.Edge e: representation.getNeighbours(s)){
            neighbourhoodLabels.add(this.function.hashString(e.getEdgeName()+" "+ previousRelabelNode(e.getNode()),
                                                Charset.defaultCharset())
                                     .toString());
        }

        String[] sorted = neighbourhoodLabels.toArray(new String[neighbourhoodLabels.size()]);
        Arrays.sort(sorted);

        label += " " + StringUtils.join(sorted, " ");

        return this.function.hashString(label, Charset.defaultCharset()).toString();
    }



    private Multiset<String> initSet(){
        set = HashMultiset.create();
        nodeMapping = new HashMap<>();

        Set<String> vertices = representation.getVertices();
        for(String v: vertices){
            nodeMapping.put(v, calcNewLabel(v));
        }

        for(String v: vertices){
            set.add(this.relabelNode(v));
            for(GraphModelRepresentation.Edge e: representation.getNeighbours(v)){
                String obj = e.getNode();
                if(!vertices.contains(obj)){
                    set.add(this.relabelNode(obj));
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
