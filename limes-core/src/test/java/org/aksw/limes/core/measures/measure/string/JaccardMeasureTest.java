package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

public class JaccardMeasureTest {

  @Test
  public void testGetSimilarity() throws Exception {
    JaccardMeasure measure = new JaccardMeasure();
    final double eps = 0.000001;
    assertEquals(1.0, measure.getSimilarity("abc", "abc"), eps);
    assertEquals(1.0, measure.getSimilarity("a b c", "a b c"), eps);
    assertEquals(1.0, measure.getSimilarity("a b c", "b c a"), eps);
    assertEquals(0.0, measure.getSimilarity("a b c", "x y z"), eps);
    assertEquals(0.5, measure.getSimilarity("a b c", "d b a"), eps);
    assertEquals(2.0/3, measure.getSimilarity("a b c d e", "x d a b c"), eps);
    assertEquals(4.0/5, measure.getSimilarity("a b c d e", "d a b c"), eps);
    assertEquals(2.0/3, measure.getSimilarity("aa ba ca da ea", "xa da aa ba ca"), eps);
  }

}
