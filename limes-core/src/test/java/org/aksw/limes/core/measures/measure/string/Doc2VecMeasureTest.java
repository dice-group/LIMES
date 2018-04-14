package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.*;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

public class Doc2VecMeasureTest {

  @Test
  public void testMeasure() {
    AMeasure measure = new Doc2VecMeasure(Doc2VecMeasure.DEFAULT_PRECOMPUTED_VECTORS_FILE_PATH);
    String a = "You eat an apple";
    String b = "You are eating an apple";
    String c = "That man eats an apple";
    String d = "That man is tall";
    String e = "That woman is tall";
    String f = "That woman is small";
    String g = "That woman is old";
    String h = "Her sister is a woman";
    String ii = "His brother is a man";
    String[] strings = new String[]{a,b,c,d,e,f,g,h,ii};
    for (int i = 0; i < strings.length-1; i++) {
      for (int j = i+1; j < strings.length; j++) {
        String x = strings[i];
        String y = strings[j];
        System.out.println(x + " VS " + y + ": " + measure.getSimilarity(x, y));
      }
    }
    assertTrue(measure.getSimilarity(a,b) > measure.getSimilarity(d, ii));
  }
}

