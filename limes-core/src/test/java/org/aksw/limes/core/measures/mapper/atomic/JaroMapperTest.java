package org.aksw.limes.core.measures.mapper.atomic;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.measures.mapper.atomic.JaroMapper;
import org.aksw.limes.core.util.RandomStringGenerator;
import org.junit.Test;

public class JaroMapperTest {

    @Test
    public void test() {
	JaroMapper jm = new JaroMapper();
        deduplicationTest(jm, 1000, 1.0);
    }
    
    private void deduplicationTest(JaroMapper jm, int sourceSize, double threshold) {
        Map<String, Set<String>> sourceMap = jm.generateRandomMap(sourceSize);

        long begin = System.currentTimeMillis();
//        Mapping m1 = bruteForce(sourceMap, sourceMap, threshold);
        long end = System.currentTimeMillis();
//        System.out.println("Brute force: " + (end - begin));
//        System.out.println("Mapping size : " + (m1.getNumberofMappings()));

        begin = System.currentTimeMillis();
        Mapping m2 = jm.runLenghtOnly(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Length-Only: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
//        System.out.println("Mapping size : " + (SetOperations.difference(m1, m2)));

        begin = System.currentTimeMillis();
        m2 = jm.runWithoutPrefixFilter(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Without Prefix Filtering: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
//        System.out.println("Mapping size : " + (SetOperations.difference(m1, m2)));

        begin = System.currentTimeMillis();
        Mapping m3 = jm.run(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Full approach: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
//        System.out.println("Mapping size : " + (SetOperations.difference(m1, m3)));
    }
    

}
