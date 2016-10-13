package org.aksw.limes.core.measures.measure.resourcesets;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMeasureTest {

    @Test
    public void testGetSimilarity() throws Exception {
        SetJaccardMeasure measure = new SetJaccardMeasure();
        Set<String> a = new HashSet<>();
        a.add("ball");
        a.add("soccer");
        a.add("socks");
        Set<String> b = new HashSet<>();
        b.add("ball");
        b.add("basket");
        b.add("socks");
        assertEquals(measure.getSimilarity(a, b), 0.5d, 0.d);
    }
}