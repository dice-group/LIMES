package org.aksw.limes.core.measures.mapper.string;


import org.aksw.commons.util.Pair;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.util.RandomStringGenerator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ExactMatchMapperTest {

    @Test
    public void test() {
        int size = 1000;
        double threshold = 1.0;
        IMapper mapper = new ExactMatchMapper();
        String sourcePrefix = "s";
        String targetPrefix = "t";
        String property = "label";
        String expression = "exactmatch(s.label, t.label)";
        Pair<ACache, ACache> caches = fillRandomCachePairMatching(sourcePrefix, targetPrefix,
                property, size);
        ACache matchingValues = caches.getKey();
        caches = fillRandomCachePairMismatching(caches, sourcePrefix, targetPrefix,
                property, size);
        ACache source = caches.getKey();
        ACache target = caches.getValue();
        AMapping mapping = mapper.getMapping(source, target, property, property, expression, threshold);
        assertEquals(mapping.getMap().keySet(), new HashSet<>(matchingValues.getAllUris()));
        for (String s : mapping.getMap().keySet()) {
            HashMap<String, Double> map = mapping.getMap().get(s);
            assertEquals(
                    map.keySet().stream()
                            .map(x -> x.substring(sourcePrefix.length()))
                            .collect(Collectors.toSet()),
                    new HashSet<>(Arrays.asList(s.substring(targetPrefix.length()))));
            assertEquals(new HashSet<>(map.values()), new HashSet<>(Arrays.asList(1.0d)));
        }

    }

    private Pair<ACache, ACache> fillRandomCachePairMatching(String sourcePrefix, String targetPrefix,
                                                             String property, int size) {
        RandomStringGenerator rsg = new RandomStringGenerator(5, 20);
        Pair<ACache, ACache> caches = new Pair<>(new MemoryCache(), new MemoryCache());
        while (caches.getKey().size() < size) {
            String s = rsg.generateString();
            caches.getKey().addTriple(sourcePrefix + "." + s, property, s);
            caches.getValue().addTriple(targetPrefix + "." + s, property, s);
        }
        return caches;
    }

    private Pair<ACache, ACache> fillRandomCachePairMismatching(Pair<ACache, ACache> caches,
                                                                String sourcePrefix, String targetPrefix,
                                                                String property, int size) {
        RandomStringGenerator rsg = new RandomStringGenerator(5, 20);
        while (caches.getKey().size() < size) {
            String s, t;
            // ensure mismatching values
            do {
                s = rsg.generateString();
            } while (caches.getKey().getInstance(targetPrefix + "." + s) != null);
            do {
                t = rsg.generateString();
            } while (!t.equals(s) &&
                    caches.getKey().getInstance(sourcePrefix + "." + t) != null);
            caches.getKey().addTriple(sourcePrefix + "." + s, property, s);
            caches.getValue().addTriple(targetPrefix + "." + t, property, t);
        }
        return caches;
    }
}
