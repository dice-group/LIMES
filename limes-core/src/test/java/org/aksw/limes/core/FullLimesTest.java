package org.aksw.limes.core;

import java.io.File;
import java.nio.file.Paths;
import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.measures.measure.string.DictionaryUtil;
import org.junit.Before;
import org.junit.Test;

public class FullLimesTest {
  @Before
  public void setUp() {
    System.out.println(new File("").getAbsolutePath());
    DictionaryUtil.initInstance(Paths.get("src/test/resources/en-de-freq.txt"));
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
}
