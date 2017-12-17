package org.aksw.limes.core;

import java.io.File;
import org.aksw.limes.core.controller.Controller;
import org.junit.Test;

public class FullLimesTest {

  @Test
  public void testMainDating() throws Exception {
    String configPath = "src/test/resources/dating-config.xml";
    System.out.println(new File("").getAbsolutePath());
    Controller.main(new String[]{configPath});
  }

  @Test
  public void testMainHobbies() throws Exception {
    String configPath = "src/test/resources/hobbies-config.xml";
    System.out.println(new File("").getAbsolutePath());
    Controller.main(new String[]{configPath});
  }
}
