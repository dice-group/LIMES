package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

public class DictionaryUtilTest {

  @Before
  public void setUp() {
    WordFrequencies wf = WordFrequencies.fromWordFrequencyFile(Paths.get("src/test/resources/small-freq.txt"));
    DictionaryUtil.initInstance(wf);
  }

  @Test
  public void testDistance() {
    assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("CA", "ABC"));
    assertEquals(3, DictionaryUtil.damerauLevenshteinDistance("abcdefg", "acbedgf"));
    assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("abcd", "bac"));
    assertEquals(3, DictionaryUtil.damerauLevenshteinDistance("abcd", "da"));
  }

  @Test
  public void testCorrectSpelling() {
    DictionaryUtil du = DictionaryUtil.getInstance();
    assertEquals("universität", du.correctSpelling("universitätt"));
    assertEquals("custody", du.correctSpelling("cusstody"));
    assertEquals("verständnis", du.correctSpelling("verständnsi"));
    assertEquals("fußball", du.correctSpelling("fßbal"));
    assertEquals("the", du.correctSpelling("tze"));
  }

}