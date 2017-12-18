package org.aksw.limes.core;

import java.io.File;
import java.nio.file.Paths;
import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.measures.measure.string.ADictionary;
import org.aksw.limes.core.measures.measure.string.bilang.BilangDictionary;
import org.aksw.limes.core.ml.algorithm.eagle.genes.AddMetric;
import org.junit.Test;

public class FullLimesTest {

  @Test
  public void testMainDating() {
    System.out.println(new File("").getAbsolutePath());
    String configPath = "src/test/resources/dating-config.xml";
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainHobbies() {
    System.out.println(new File("").getAbsolutePath());
    String configPath = "src/test/resources/hobbies-config.xml";
    ADictionary defaultDictionary = new BilangDictionary(Paths.get("src/test/resources/en-de-small.txt"));
    ADictionary.setDefaultDictionary(defaultDictionary);
    Controller.main(new String[]{configPath});
  }
}
