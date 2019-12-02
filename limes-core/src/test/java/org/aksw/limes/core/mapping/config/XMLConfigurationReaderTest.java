package org.aksw.limes.core.mapping.config;

import static org.junit.Assert.*;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.junit.Test;

public class XMLConfigurationReaderTest {

    @Test
    public void test1() {
        String filename = System.getProperty("user.dir") + "/src/main/resources/lgd-lgd.xml";
        XMLConfigurationReader reader = new XMLConfigurationReader(filename);
        Configuration config = reader.read();
        System.out.println(config.getExpectedSelectivity());
        System.out.println(config.getOptimizationTime());
    }

}
