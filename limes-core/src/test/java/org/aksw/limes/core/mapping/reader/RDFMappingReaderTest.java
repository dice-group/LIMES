package org.aksw.limes.core.mapping.reader;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.junit.Test;

public class RDFMappingReaderTest {
	
	@Test
	public void csvMappingThreeColTester(){
		Mapping testMap = MappingFactory.createDefaultMapping();
		testMap.add("http://dbpedia.org/resource/Berlin","http://linkedgeodata.org/triplify/node240109189", 1.0d);
		testMap.setPredicate("http://www.w3.org/2002/07/owl#sameAs");
		
		RDFMappingReader r = new RDFMappingReader();
		Mapping readMap = r.read("/resources/mapping-test.nt");
		
		assertTrue(readMap.equals(testMap));
	}

}
