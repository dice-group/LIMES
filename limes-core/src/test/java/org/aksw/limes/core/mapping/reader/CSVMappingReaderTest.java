package org.aksw.limes.core.mapping.reader;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.junit.Test;

public class CSVMappingReaderTest {
	
	@Test
	public void csvMappingThreeColTester(){
		Mapping testMap = new MemoryMapping();
		testMap.add("http://dbpedia.org/resource/Berlin","http://linkedgeodata.org/triplify/node240109189", 1.0d);
		testMap.setPredicate("http://www.w3.org/2002/07/owl#sameAs");
		
		CSVMappingReader r = new CSVMappingReader(",");
		Mapping readMap = r.read("/resources/mapping-3col-test.csv");
		
		assertTrue(readMap.equals(testMap));
	}
	
	@Test
	public void csvMappingThreeColWithSimilarityTester(){
		Mapping testMap = new MemoryMapping();
		testMap.add("http://dbpedia.org/resource/Berlin","http://linkedgeodata.org/triplify/node240109189", 0.999d);
		
		CSVMappingReader r = new CSVMappingReader(",");
		Mapping readMap = r.read("/resources/mapping-3col-sim-test.csv");
		
		assertTrue(readMap.equals(testMap));
	}
	
	@Test
	public void csvMappingTwoColTester(){
		Mapping testMap = new MemoryMapping();
		testMap.add("http://dbpedia.org/resource/Berlin","http://linkedgeodata.org/triplify/node240109189", 1d);
		
		CSVMappingReader r = new CSVMappingReader(",");
		Mapping readMap = r.read("/resources/mapping-2col-test.csv");
		
		assertTrue(readMap.equals(testMap));
	}

}
