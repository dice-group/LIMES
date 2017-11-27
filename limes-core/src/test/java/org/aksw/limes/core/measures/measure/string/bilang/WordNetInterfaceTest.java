package org.aksw.limes.core.measures.measure.string.bilang;

import static org.junit.Assert.*;

import org.junit.Test;

public class WordNetInterfaceTest {

  @Test
  public void testSimilarity() {
    WordNetInterface wn = new WordNetInterface("src/test/resources/WordNet-3.0");
    double eps = 0.00001;
    assertEquals(1.0, wn.getSimilarity("dog", "dog"), eps);
    assertEquals(0.0, wn.getSimilarity("cat", "between"), eps);
    assertEquals(1.0, wn.getSimilarity("listen", "hear"), eps);
    assertTrue(wn.getSimilarity("dog", "rocket") < wn.getSimilarity("dog", "mammal"));
    assertTrue(wn.getSimilarity("draw", "cook") > wn.getSimilarity("enter", "sign"));
    assertTrue(wn.getSimilarity("draw", "cook") < wn.getSimilarity("draw", "paint"));
  }

}