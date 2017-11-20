package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.*;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

public class HammingDistanceMeasureTest {

  @Test
  public void testGetSimilarity() throws Exception {
    AMeasure measure = new HammingDistanceMeasure();
    final double eps = 0.000001;
    assertEquals(1.0, measure.getSimilarity("", ""), eps);
    assertEquals(1.0, measure.getSimilarity("abc", "abc"), eps);
    assertEquals(0.0, measure.getSimilarity("abc", "bca"), eps);
    assertEquals(1.0/3, measure.getSimilarity("abc", "bac"), eps);
    assertEquals(0.0, measure.getSimilarity("abc", "xyz"), eps);
    assertEquals(0.5, measure.getSimilarity("abcd", "xbcy"), eps);
    try {
      measure.getSimilarity("abcde", "xyza");
    } catch (IllegalArgumentException e) {
      return;
    }
    fail("should have thrown an exception");
  }

}