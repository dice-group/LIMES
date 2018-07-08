package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.jaccard;

import java.util.HashMap;
import java.util.Map;

public class JaccardIndex {

    private NgramIndexer indexer;

    public JaccardIndex(int window){
        indexer = new NgramIndexer(window);
    }

    public void index(String s){
        indexer.index(s);
    }

    public Map<String, Double> getNearestStrings(String s, double threshold){
        Map<String, Double> out = new HashMap<>();

        Map<String, NgramIndexer.NgramStats> nearest = indexer.getOverlaps(s);

        for(Map.Entry<String, NgramIndexer.NgramStats> e: nearest.entrySet()){
            NgramIndexer.NgramStats stats = e.getValue();

            if(stats.getCommonCount() < threshold * stats.getSrcNgramsCount()
                    || stats.getCommonCount() < threshold * (stats.getCommonCount()+stats.getUncommonCount()))
                continue;


            double jaccard = (double)stats.getCommonCount() / (stats.getSrcNgramsCount() + stats.getUncommonCount());

            if(jaccard >= threshold){
                out.put(e.getKey(), jaccard);
            }
        }
        return out;
     }
}
