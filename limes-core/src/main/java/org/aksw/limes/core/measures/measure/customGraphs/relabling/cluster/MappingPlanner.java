package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
//import com.sun.tools.javac.code.Type;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.customGraphs.MapAndLog;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappingPlanner {

    private static final int CHUNK_SIZE = 3000;

    private List<Set<String>> partitions;

    public MappingPlanner(Set<String> set) {
        initPartition(set);
    }


    private void initPartition(Set<String> set){
        if(set.size() <= CHUNK_SIZE){
            this.partitions = new ArrayList<>(1);
            this.partitions.add(set);
        }else {
            int chunks = IntMath.divide(set.size(), CHUNK_SIZE, RoundingMode.UP);
            int indChunkSize = IntMath.divide(set.size(), chunks, RoundingMode.UP);
            this.partitions = new ArrayList<>();

            Set<String> buffer = new HashSet<>(indChunkSize);

            for(String s: set){

                buffer.add(s);

                if(buffer.size() >= indChunkSize){
                    this.partitions.add(buffer);
                    buffer = new HashSet<>(indChunkSize);
                }

            }

            if(!buffer.isEmpty())
                this.partitions.add(buffer);

        }
    }

    public AMapping execute(SimilarityFilter filter){

        MapAndLog<Integer, Integer> log = new MapAndLog<>(
                x -> x, x -> String.format("Processed chunk %d", x), "MappingPlanner",
                partitions.size() * partitions.size()
        );

        AMapping mapping = MappingFactory.createDefaultMapping();

        for(int i = 0; i < partitions.size(); i++){
            for(int j = 0; j < partitions.size(); j++){
                mapping.getMap().putAll(
                        new MappingChunk(partitions.get(i), partitions.get(j)).execute(filter).getMap()
                );
                log.apply(i*partitions.size() + j);
            }
        }

        return mapping;

    }


    private class MappingChunk{

        private Set<String> A;
        private Set<String> B;

        public MappingChunk(Set<String> a, Set<String> b) {
            A = a;
            B = b;
        }

        public AMapping execute(SimilarityFilter filter){
            return MappingHelper.filter(A, B, filter);
        }

    }

}
