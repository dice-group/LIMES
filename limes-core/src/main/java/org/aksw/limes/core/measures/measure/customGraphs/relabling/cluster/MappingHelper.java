package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.MapperFactory;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;

import java.util.Set;

/**
 * A helper class for mappings
 *
 * @author Cedric Richter
 */

public class MappingHelper {

    public static ACache initMap(Set<String> str){
        MemoryCache cache = new MemoryCache();

        for(String s: str){
            cache.addTriple(s, "content", s);
        }

        return cache;
    }

    public static String getMeasureName(MeasureType type){
        switch (type){
            case JARO:
                return MeasureFactory.JARO;
            case JAROWINKLER:
                return MeasureFactory.JAROWINKLER;
            case COSINE:
                return MeasureFactory.COSINE;
            case QGRAMS:
                return MeasureFactory.QGRAMS;
            case TRIGRAM:
                return MeasureFactory.TRIGRAM;
            case JACCARD:
                return MeasureFactory.JACCARD;
            case LEVENSHTEIN:
                return MeasureFactory.LEVENSHTEIN;
        }
        return MeasureFactory.LEVENSHTEIN;
    }

    public static AMapping executeTrigram(Set<String> A, Set<String> B,  double threshold){
        return FastNGramMapper.compute(A, B, 3, threshold);
    }

    /**
     * Similarity mapper for strings are defined over instances.
     * This method allow to use the mapper without creating fake instances before
     * @param set the source instances
     * @param filter defined mapping operation
     * @return a mapping from source instances to source instances
     */
    public static AMapping filter(Set<String> set, SimilarityFilter filter){
       return filter(set, set, filter);
    }

    /**
     * Similarity mapper for strings are defined over instances.
     * This method allow to use the mapper without creating fake instances before
     * @param A the source instances
     * @param B the target instances
     * @param filter defined mapping operation
     * @return a mapping from source instances to target instances
     */
    public static AMapping filter(Set<String> A, Set<String> B,  SimilarityFilter filter){
        if(filter.getSimilarityType().equals(MeasureType.TRIGRAM)){
            return executeTrigram(A, B, filter.getThreshold());
        }

        ACache mapA = initMap(A);
        ACache mapB = initMap(B);

        AMapper mapper = MapperFactory.createMapper(filter.getSimilarityType());

        String expression = getMeasureName(filter.getSimilarityType())+"(x.content, y.content)";

        return mapper.getMapping(mapA, mapB, "x", "y", expression, filter.getThreshold());

    }


}
