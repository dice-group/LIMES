package org.aksw.limes.core.measures.measure.customGraphs.subgraphs;

import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Cedric Richter
 */
public class WLSubgraphCollector {

    private IDescriptionGraphView graph;
    private List<WLSubgraphStore> subgraphs = new ArrayList<>();

    public WLSubgraphCollector(IDescriptionGraphView graph) {
        this.graph = graph;
    }

    WLSubgraphStore get(int i){
        if(i < 0)throw new NoSuchElementException("Can compute iteration greater equal zero only");

        if(i >= subgraphs.size()){
            if(i == 0){
                subgraphs.add(new WLSubgraphZeroStore(graph));
            }else{
                WLSubgraphStore previous = get(i - 1);
                subgraphs.add(new WLSubgraphNextStore(graph, previous));
            }
        }

        return subgraphs.get(i);
    }

    public IDescriptionGraphView getGraph() {
        return graph;
    }

    public WLSubgraphProcessor iterate(int i){
        return new WLSubgraphProcessor(this, i - 1);
    }

    public WLSubgraphProcessor iterate(){
        return iterate(1);
    }

}
