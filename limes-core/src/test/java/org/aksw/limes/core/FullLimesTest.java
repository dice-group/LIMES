package org.aksw.limes.core;

import java.io.File;
import java.nio.file.Paths;
import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.measures.measure.string.DictionaryUtil;
import org.aksw.limes.core.measures.measure.string.WordFrequencies;
import org.junit.Before;
import org.junit.Test;

public class FullLimesTest {
  
  static String basePath = "src/test/resources/datasets/";
  
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
    String configPath = basePath + "simple-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainDating() {
    String configPath = basePath + "dating-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainHobbies() {
    String configPath = basePath + "hobbies-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainSparql() {
    String configPath = basePath + "with-sparql-config.xml";
    Controller.main(new String[]{configPath});
  }
}
