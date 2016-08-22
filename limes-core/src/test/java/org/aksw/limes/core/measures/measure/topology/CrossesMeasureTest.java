package org.aksw.limes.core.measures.measure.topology;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class CrossesMeasureTest {

    @Test
    public void testGetSimilarity() throws Exception {
        AMeasure measure = new CrossesMeasure();
        assertEquals(0.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))"), 0d);
        assertEquals(1.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","LINESTRING (-20 -20, -10 0, 0 0, 0 -10)"), 0d);
    }
}