package org.aksw.limes.core;

import java.io.File;
import java.nio.file.Paths;
import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.measures.measure.string.DictionaryUtil;
import org.aksw.limes.core.measures.measure.string.WordFrequencies;
import org.junit.Before;
import org.junit.Test;

public class FullLimesTest {
  @Before
  public void setUp() {
//    System.out.println(new File("").getAbsolutePath());
//    WordFrequencies wf = WordFrequencies
//        .fromWordFrequencyFile(Paths.get("src/test/resources/en-freq.txt"));
//    wf = wf.merge(WordFrequencies.fromWordFrequencyFile(Paths.get("src/test/resources/de-freq.txt")));
//    DictionaryUtil.initInstance(wf);
  }

  @Test
  public void testMainSimple() {
    String configPath = "src/test/resources/simple-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainDating() {
    String configPath = "src/test/resources/dating-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainHobbies() {
    String configPath = "src/test/resources/hobbies-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainSparql() {
    String configPath = "src/test/resources/with-sparql-config.xml";
    Controller.main(new String[]{configPath});
  }
}
