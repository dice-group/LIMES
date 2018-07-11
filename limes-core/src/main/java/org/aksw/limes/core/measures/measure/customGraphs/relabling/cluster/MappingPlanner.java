package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;


import com.google.common.math.IntMath;

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

    private List<Set<String>> partitionsA;
    private List<Set<String>> partitionsB;


    public MappingPlanner(Set<String> A, Set<String> B) {
        partitionsA = initPartition(A);
        partitionsB = initPartition(B);
    }


    private List<Set<String>> initPartition(Set<String> set){
        if(set.size() <= CHUNK_SIZE){
            List<Set<String>> result =  new ArrayList<>(1);
            result.add(set);
            return result;
        }else {
            int chunks = IntMath.divide(set.size(), CHUNK_SIZE, RoundingMode.UP);
            int indChunkSize = IntMath.divide(set.size(), chunks, RoundingMode.UP);
            List<Set<String>> result = new ArrayList<>();

            Set<String> buffer = new HashSet<>(indChunkSize);

            for(String s: set){

                buffer.add(s);

                if(buffer.size() >= indChunkSize){
                    result.add(buffer);
                    buffer = new HashSet<>(indChunkSize);
                }

            }

            if(!buffer.isEmpty())
                result.add(buffer);

            return result;
        }
    }

    public AMapping execute(SimilarityFilter filter){

        MapAndLog<Integer, Integer> log = new MapAndLog<>(
                x -> x, x -> String.format("Processed chunk %d", x), "MappingPlanner",
                partitionsA.size() * partitionsB.size()
        );

        AMapping mapping = MappingFactory.createDefaultMapping();

        for(int i = 0; i < partitionsA.size(); i++){
            for(int j = 0; j < partitionsB.size(); j++){
                mapping.getMap().putAll(
                        new MappingChunk(partitionsA.get(i), partitionsB.get(j)).execute(filter).getMap()
                );
                log.apply(i*partitionsA.size() + j);
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
