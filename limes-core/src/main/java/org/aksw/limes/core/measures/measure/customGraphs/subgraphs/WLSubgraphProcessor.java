package org.aksw.limes.core.measures.measure.customGraphs.subgraphs;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Cedric Richter
 */
public class WLSubgraphProcessor {

    private WLSubgraphCollector parent;
    private int iteration;

    WLSubgraphProcessor(WLSubgraphCollector parent, int iteration) {
        this.parent = parent;
        this.iteration = iteration;
    }

    public int getIteration(){
        return iteration;
    }

    public WLSubgraphProcessor iterate(int i){
        return new WLSubgraphProcessor(parent, iteration + i);
    }

    public WLSubgraphProcessor iterate(){
        return iterate(1);
    }

    public Multiset<String> collect(){
        return parent.get(iteration).getSubgraphCertificates();
    }

    public List<Multiset<String>> collectAll(){
        List<Multiset<String>> list = new ArrayList<>();
        for(int i = 0; i <= iteration; i++){
            list.add(parent.get(i).getSubgraphCertificates());
        }
        return list;
    }

    public Stream<Multiset<String>> streamAll(){
        return IntStream.rangeClosed(0, iteration).mapToObj(x -> parent.get(x).getSubgraphCertificates());
    }

    public IDescriptionGraphView getGraph(){
        return parent.getGraph();
    }

}
