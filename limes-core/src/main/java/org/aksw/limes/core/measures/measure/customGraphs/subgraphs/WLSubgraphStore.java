package org.aksw.limes.core.measures.measure.customGraphs.subgraphs;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.description.INode;

/**
 * @author Cedric Richter
 */
public abstract class WLSubgraphStore {

    protected IDescriptionGraphView view;
    private Multiset<String> cache;

    public WLSubgraphStore(IDescriptionGraphView view) {
        this.view = view;
    }

    public Multiset<String> getSubgraphCertificates(){
        if(cache == null){
            cache = HashMultiset.create();
            for(INode node: view.getNodesAndLeaves()){
                String label = map(node);

                if(label != null)
                    cache.add(label);

            }
        }
        return  cache;
    }

    public abstract String map(INode node_uri);

}
