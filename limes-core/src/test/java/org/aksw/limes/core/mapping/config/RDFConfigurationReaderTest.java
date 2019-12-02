package org.aksw.limes.core.mapping.config;

import static org.junit.Assert.*;

import java.util.Set;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;

import org.junit.Test;

public class RDFConfigurationReaderTest {

    @Test
    public void test1() {
        String filename = System.getProperty("user.dir") + "/src/main/resources/lgd-lgd.ttl";
        RDFConfigurationReader reader = new RDFConfigurationReader(filename);
        Configuration config = reader.read();
        
        Set<String> parameters = config.getConfigurationParametersNames();
        assertTrue(parameters.contains("optimizationTime"));
        assertTrue(parameters.contains("expectedSelectivity"));
        
        assertTrue(config.getExpectedSelectivity() == 0.8);
        assertTrue(config.getOptimizationTime() == 1000);
        
        
        
    }
    
    @Test
    public void test2() {
        String filename = System.getProperty("user.dir") + "/src/main/resources/lgd-lgd2.ttl";
        RDFConfigurationReader reader = new RDFConfigurationReader(filename);
        Configuration config = reader.read();
        
        
        assertTrue(config.getExpectedSelectivity() == 1.0);
        assertTrue(config.getOptimizationTime() == 0);
        
        
        
    }

}
