package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.aksw.limes.core.io.mapping.AMapping;
import scala.runtime.StringFormat$;

import java.util.*;

public abstract class ASimilarityAggregator {


    public Table<String, String, Double> getSimilarities(Set<String> A, Set<String> B, List<SimilarityFilter> filters){
        Table<String, String, Double> table = null;

        Set<String> activeSetA = new HashSet<>(A);
        Set<String> activeSetB = new HashSet<>(B);

        for(SimilarityFilter filter: filters){
            AMapping mapping = executeFilter(activeSetA, activeSetB, filter);

            if(table == null){
                table = initTable(mapping, filter.getThreshold());
            }else{
                table = aggregateTables(table, mapping, filter.getThreshold());
            }

            activeSetA = aggregateKeys(activeSetA, mapping.getMap().keySet());

            Set<String> keys = new HashSet<>();
            for(Map<String, Double> m: mapping.getMap().values())
                keys.addAll(m.keySet());

            activeSetB = aggregateKeys(activeSetB, keys);
        }

        if(table == null){
            table = HashBasedTable.create();
        }else{
            Table<String, String, Double> intermediate = HashBasedTable.create();
            for(Table.Cell<String, String, Double> cell: table.cellSet()){
                intermediate.put(cell.getRowKey(), cell.getColumnKey(), finalMap(cell.getValue()));
            }
            table = intermediate;
        }

        return table;
    }

    protected AMapping executeFilter(Set<String> A, Set<String> B, SimilarityFilter filter){
        return new MappingPlanner(A, B).execute(filter);
    }

    protected Table<String, String, Double> initTable(AMapping mapping, double threshold){
        Table<String, String, Double> result = HashBasedTable.create();

        for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){
            for(Map.Entry<String, Double> t: e.getValue().entrySet()){
                if(e.getKey().equals(t.getKey()))continue;
                double dist = intermediateMap(t.getValue(), threshold);
                result.put(e.getKey(), t.getKey(), dist*dist);
            }
        }

        return result;
    }

    protected double intermediateMap(double similarity, double threshold){
        return 1 - ((similarity - threshold) / ( 1 - threshold));
    }

    protected double finalMap(double similarity){
        return (-1)*Math.sqrt(similarity);
    }

    protected abstract Table<String, String, Double> aggregateTables(Table<String, String, Double> current,
                                                                     AMapping mapping, double threshold);

    protected abstract Set<String> aggregateKeys(Set<String> activeKeys, Set<String> filtered);

}
