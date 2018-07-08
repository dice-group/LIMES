package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.aksw.limes.core.io.mapping.AMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ORSimilarityAggregator extends ASimilarityAggregator {

    @Override
    protected Table<String, String, Double> aggregateTables(Table<String, String, Double> current, AMapping mapping, double threshold) {
        Table<String, String, Double> ret = HashBasedTable.create();

        for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){
            for(Map.Entry<String, Double> t: e.getValue().entrySet()){
                if(e.getKey().equals(t.getKey()))continue;

                double baseVal = current.contains(e.getKey(), t.getKey())?current.get(e.getKey(), t.getKey()):0.0;

                double distance = intermediateMap(t.getValue(), threshold);
                ret.put(e.getKey(), t.getKey(),
                        baseVal + distance*distance);

            }
        }

        return ret;
    }

    @Override
    protected Set<String> aggregateKeys(Set<String> activeKeys, Set<String> filtered) {
        return activeKeys;
    }
}
