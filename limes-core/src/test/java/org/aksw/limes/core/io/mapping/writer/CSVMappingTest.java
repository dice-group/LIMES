package org.aksw.limes.core.io.mapping.writer;


import java.io.IOException;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;


public class CSVMappingTest {

    AMapping mapping = MappingFactory.createDefaultMapping();

    @Before
    public void init() {
        mapping.add("foo:a", "foo:b", 1d);
        mapping.add("aa", "bb", 1d);
        mapping.add("foo:aaaa", "foo:bb", 0.8d);
    }
    
    @Test
    public void testReadMappingFromRDF() throws IOException {
        
        String outputFile   = System.getProperty("user.home")+"/";
        outputFile += "test";

        (new CSVMappingWriter()).write(mapping, outputFile);
        
    }

}
