package org.aksw.limes.core.measures.measure.customGraphs.subgraphs;

import com.google.common.base.Charsets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.description.IEdge;
import org.aksw.limes.core.measures.measure.customGraphs.description.INode;
import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Cedric Richter
 */
public class WLSubgraphNextStore extends WLSubgraphStore {

    private static final int hash_seed = 78490;

    private WLSubgraphStore last;
    private Map<INode, String> labelCache = new HashMap<>();
    private HashFunction function;

    public WLSubgraphNextStore(IDescriptionGraphView view, WLSubgraphStore last) {
        super(view);
        this.last = last;
        this.function = Hashing.murmur3_128(hash_seed);
    }

    @Override
    public String map(INode node_uri) {
        if(!labelCache.containsKey(node_uri)) {
            if(!view.getNodes().contains(node_uri)){
                return last.map(node_uri);
            }

            SortedMultiset<String> neighbours = TreeMultiset.create();

            for(IEdge e: view.getNeighbours(node_uri).values()){
                String targetLabel = last.map(e.getTarget());
                if(targetLabel != null){
                    if(e.getEdgeType() != null){
                        targetLabel = e.getEdgeType() + " " + targetLabel;
                    }
                    neighbours.add(targetLabel);
                }
            }

            String label = last.map(node_uri);
            label = label==null?"":label;
            label += " " + StringUtils.join(neighbours.iterator(), " ");
            label = this.function.hashString(label, Charsets.UTF_8).toString();

            labelCache.put(node_uri, label);
        }
        return labelCache.get(node_uri);
    }
}
