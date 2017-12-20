package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

public class DictionaryUtilTest {

  @Before
  public void setUp() {
    DictionaryUtil.initInstance(Paths.get("src/test/resources/en-de-freq.txt"));
  }

  @Test
  public void testDistance() {
    DictionaryUtil du = DictionaryUtil.getInstance();
    assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("CA","ABC"));
    assertEquals(3, DictionaryUtil.damerauLevenshteinDistance("abcdefg","acbedgf"));
    assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("abcd","bac"));
    assertEquals(3, DictionaryUtil.damerauLevenshteinDistance("abcd","da"));
  }
  @Test
  public void testCorrectSpelling() {
    DictionaryUtil du = DictionaryUtil.getInstance();
    assertEquals("custody", du.correctSpelling("cusstody"));
    assertEquals("universität", du.correctSpelling("universitt"));
    assertEquals("verständnis", du.correctSpelling("verständnsi"));
    assertEquals("fußball", du.correctSpelling("fßbal"));
  }

}