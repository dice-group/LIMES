package org.aksw.limes.core.measures.measure.topology;

import org.aksw.limes.core.measures.measure.Measure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class EqualsMeasureTest {

    @Test
    public void testGetSimilarity() throws Exception {
        Measure measure = new EqualsMeasure();
        assertEquals(1.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))"), 0d);
        assertEquals(0.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","POLYGON ((-20 -20, -10 0, 0 0, 0 -10, -20 -20))"), 0d);
    }
}