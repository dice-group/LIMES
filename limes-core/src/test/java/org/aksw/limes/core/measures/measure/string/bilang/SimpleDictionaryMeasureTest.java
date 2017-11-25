package org.aksw.limes.core.measures.measure.string.bilang;

import java.nio.file.Paths;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleDictionaryMeasureTest {
  @Test
  public void testSimilarity() {
    BilangDictionary d = new BilangDictionary(Paths.get("src/test/resources/en-de-small.txt"));
    System.out.println(d.sourceSize());
    System.out.println(d.targetSize());
    AMeasure measure = new SimpleDictionaryMeasure(d);
    System.out.println(measure.getSimilarity("dog","Katze") + " " + measure.getSimilarity("dog", "Hund"));
    System.out.println(measure.getSimilarity("dogg","huund") + " " + measure.getSimilarity("dog", "Hund"));
    assertTrue(measure.getSimilarity("dog","Katze") < measure.getSimilarity("dog", "Hund"));
    assertTrue(measure.getSimilarity("dogg","huund") < measure.getSimilarity("dog", "Hund"));
    assertTrue(measure.getSimilarity("dog","hund") == measure.getSimilarity("dog", "Hund"));

  }
}
