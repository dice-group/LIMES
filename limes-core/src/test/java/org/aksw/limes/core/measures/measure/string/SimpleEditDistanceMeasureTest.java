package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

public class SimpleEditDistanceMeasureTest {

  @Test
  public void testGetWorstCaseCost() {
    SimpleEditDistanceMeasure measure = new SimpleEditDistanceMeasure(0, 2, 3, 4);
    assertEquals(24, measure.getWorstCaseCost(6,6));
    assertEquals(24+6, measure.getWorstCaseCost(6,9));
    assertEquals(24+12, measure.getWorstCaseCost(10,6));
    measure = new SimpleEditDistanceMeasure(0, 2, 1, 4);
    assertEquals(19, measure.getWorstCaseCost(7,6));
  }

  @Test
  public void testGetSimilarity() {
    SimpleEditDistanceMeasure measure = new SimpleEditDistanceMeasure(0, 2, 3, 4);
    final double eps = 0.000001;
    assertEquals(1.0, measure.getSimilarity("", ""), eps);
    assertEquals(1.0, measure.getSimilarity("abc", "abc"), eps);
    assertEquals(0.0, measure.getSimilarity("abcd", "xyz"), eps);
    assertEquals(1.0 - 5.0/measure.getWorstCaseCost(3,3), measure.getSimilarity("abc", "bca"), eps);
    assertEquals(1.0 - 10.0/measure.getWorstCaseCost(4,5), measure.getSimilarity("abcd", "xbcyz"), eps);

  }
}
