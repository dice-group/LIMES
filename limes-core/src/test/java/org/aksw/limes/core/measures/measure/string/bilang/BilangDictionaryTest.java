package org.aksw.limes.core.measures.measure.string.bilang;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import org.aksw.limes.core.measures.measure.string.ADictionary;
import org.junit.Test;

public class BilangDictionaryTest {

  @Test
  public void testCorrectSpelling() {
    BilangDictionary d = new BilangDictionary(Paths.get("src/test/resources/en-de-small.txt"));
    assertEquals("custody", d.correctSpelling("cusstody"));
    assertEquals("universität", d.correctSpelling("universitt"));
    assertEquals("verständnis", d.correctSpelling("verständnsi"));
  }

}