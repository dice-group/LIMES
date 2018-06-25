package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.controller.Controller;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class is for testing a complete run of LIMES. Given a configuration XML file, we start
 * LIMES to see if the new string measures work.
 *
 * @author Swante Scholz
 */
public class FullLimesTest {
    
    static String basePath = "src/test/resources/";
    
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
    
    @Test
    public void testMainDoc2Vec() {
        String configPath = basePath + "descriptions-config.xml";
        Controller.main(new String[]{configPath});
    }
}
