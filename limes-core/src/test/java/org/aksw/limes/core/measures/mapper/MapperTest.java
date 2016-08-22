package org.aksw.limes.core.measures.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.util.RandomStringGenerator;

/**
 * @author Kevin Dreßler
 * @version %I%, %G%
 * @since 1.0
 */
public class MapperTest {
    public static AMapping bruteForce(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
                                      double threshold, AMeasure measure) {
        AMapping m = MappingFactory.createDefaultMapping();
        double sim;
        for (String s : sourceMap.keySet()) {
            for (String t : targetMap.keySet()) {
                sim = measure.getSimilarity(s, t);
                if (sim >= threshold) {
                    for (@SuppressWarnings("unused")
                    String sourceUris : sourceMap.get(s)) {
                        for (@SuppressWarnings("unused")
                        String targetUris : targetMap.get(t)) {
                            m.add(s, t, sim);
                        }
                    }
                }
            }
        }
        return m;
    }

    public Map<String, Set<String>> generateRandomMap(int size) {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        RandomStringGenerator rsg = new RandomStringGenerator(5, 20);
        while (map.size() < size) {
            String s = rsg.generateString();
            Set<String> set = new HashSet<String>();
            set.add(s);
            map.put(s, set);
        }
        return map;
    }
}
