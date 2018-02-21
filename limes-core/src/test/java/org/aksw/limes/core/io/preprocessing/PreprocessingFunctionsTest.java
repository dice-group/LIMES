package org.aksw.limes.core.io.preprocessing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction.IllegalNumberOfPropertiesException;
import org.aksw.limes.core.io.preprocessing.functions.ToUppercase;
import org.aksw.limes.core.io.query.FileQueryModule;
import org.junit.Before;
import org.junit.Test;

public class PreprocessingFunctionsTest {
	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";
	public static final String UPPERCASE_EXPECTED = "IBUPROFEN@DE";
	public HybridCache cache;

	@Before
	public void prepareData(){
        HashMap<String, String> prefixes = new HashMap<>();
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("dbpo", "http://dbpedia.org/ontology/");

        Map<String, Map<String, String>> functions = new HashMap<>();

        KBInfo kbinfo = new KBInfo(
                "DBpedia",                                                            //String id
//                "resources/ibuprofen.nt",                                            //String endpoint
                Thread.currentThread().getContextClassLoader().getResource("ibuprofen.nt").getPath(),
                null,                                                                //String graph
                "?x",                                                                //String var
                new ArrayList<String>(Arrays.asList("rdfs:label")),                    //List<String> properties
                null,                    //List<String> optionlProperties
                new ArrayList<String>(Arrays.asList("?x rdf:type dbpo:Drug")),        //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
                prefixes,                                                            //Map<String, String> prefixes
                1000,                                                                //int pageSize
                "N3",                                                                //String type
                -1,                                                               //int minOffset
                -1                                                                //int maxoffset
        );
        FileQueryModule fqm = new FileQueryModule(kbinfo);
        cache = new HybridCache();
        fqm.fillCache(cache);
	}
	
	@Test
	public void testUppercase() throws IllegalNumberOfPropertiesException{
		new ToUppercase().applyFunction(cache, "rdfs:label");
		assertTrue(cache.size() > 0);
		assertEquals(UPPERCASE_EXPECTED, cache.getInstance(TEST_INSTANCE).getProperty("rdfs:label").first());
	}
	
	@Test
	public void testUppercaseWrongParameterNumber(){
		try{
            new ToUppercase().applyFunction(cache, new String[0]);
            assertFalse(true);
		}catch(IllegalNumberOfPropertiesException e){
		}
	}
}
