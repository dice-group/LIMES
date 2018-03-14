package org.aksw.limes.core.io.preprocessing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Before;
import org.junit.Test;

public class NEWProprocessorTest {

	public HybridCache cache;
	public KBInfo kbInfo;
	public static LinkedHashMap<String, Map<String, String>> functions;
	
	public static final String TEST_INSTANCE = "testInstance";

	public static final String PROP_LABEL = "testLabel1";
	public static final String PROP_RENAMED_LABEL = "renamedLabel1";
	public static final String PROP_NUMBER = "number";
	public static final String PROP_CONCAT = "concat";

	public static final String INST1_PROP_NUMBER_VALUE = "10^^http://www.w3.org/2001/XMLSchema#positiveInteger";
	public static final String INST1_PROP_LABEL_VALUE1 = "testLabel1";
	public static final String INST1_PROP_LABEL_VALUE2 = "testLabel2";
	
	public static final String INST1_PROP_NUMBER_VALUE_EXPECTED = "10";
	public static final String INST1_PROP_LABEL_VALUE1_EXPECTED = "label1";
	public static final String INST1_PROP_LABEL_VALUE2_EXPECTED = "label2";
	public static final String INST1_PROP_CONCAT_VALUE1_EXPECTED = "label110";
	public static final String INST1_PROP_CONCAT_VALUE2_EXPECTED = "label210";

	public static final String INST2_PROP_NUMBER_VALUE = "-4^^http://www.w3.org/2001/XMLSchema#Integer";
	public static final String INST2_PROP_LABEL_VALUE1 = "testLabel3";
	public static final String INST2_PROP_LABEL_VALUE2 = "testLabel4";
	public static final String INST2_PROP_CONCAT_VALUE1_EXPECTED = "label3-4";
	public static final String INST2_PROP_CONCAT_VALUE2_EXPECTED = "label4-4";

	public static final String INST2_PROP_NUMBER_VALUE_EXPECTED = "-4";
	public static final String INST2_PROP_LABEL_VALUE1_EXPECTED = "label3";
	public static final String INST2_PROP_LABEL_VALUE2_EXPECTED = "label4";

	public static final String INST3_PROP_NUMBER_VALUE = "0.05^^http://www.w3.org/2001/XMLSchema#Decimal";
	public static final String INST3_PROP_LABEL_VALUE1 = "testLabel5";
	public static final String INST3_PROP_LABEL_VALUE2 = "testLabel6";

	public static final String INST3_PROP_NUMBER_VALUE_EXPECTED = "0.05";
	public static final String INST3_PROP_LABEL_VALUE1_EXPECTED = "label5";
	public static final String INST3_PROP_LABEL_VALUE2_EXPECTED = "label6";
	public static final String INST3_PROP_CONCAT_VALUE1_EXPECTED = "label50.05";
	public static final String INST3_PROP_CONCAT_VALUE2_EXPECTED = "label60.05";
	
	
	public static final String FUNCTIONID1_EXPECTED = "lowercase";
	public static final String FUNCTIONID2_EXPECTED = "replace";
	public static final String FUNCTIONID3_EXPECTED = "concat";
	
	public static Instance i1expected;
	public static Instance i2expected;
	public static Instance i3expected;

	@Before
	public void prepareData() {
		functions = new LinkedHashMap<>();

        HashMap<String, String> f1 = new HashMap<>();
        f1.put(PROP_RENAMED_LABEL, "lowercase->replace(test,)");
        functions.put(PROP_LABEL, f1);
        HashMap<String, String> f2 = new HashMap<>();
        f2.put(PROP_NUMBER, PROP_NUMBER);
        functions.put(PROP_NUMBER, f2);
        HashMap<String, String> f3 = new HashMap<>();
        f3.put(PROP_CONCAT, "concat("+PROP_RENAMED_LABEL +","+PROP_NUMBER+")");
        functions.put(PROP_CONCAT, f3);
		
        kbInfo = new KBInfo(
                "DBpedia",                                                       //String id
                "http://dbpedia.org/sparql",                                     //String endpoint
                null,                                                            //String graph
                "?x",                                                            //String var
                new ArrayList<String>(Arrays.asList("testLabel1","number")),              //List<String> properties
                null,                                                            //List<String> optionlProperties
                new ArrayList<String>(Arrays.asList("")),   //ArrayList<String> restrictions
                functions,                                                       //Map<String, Map<String, String>> functions
                new HashMap<String,String>(),                                                        //Map<String, String> prefixes
                1000,                                                            //int pageSize
                "sparql",                                                         //String type
                -1,                                                               //int minOffset
                -1                                                                //int maxoffset
        );
		cache = new HybridCache();
		Instance testInstance1 = new Instance(TEST_INSTANCE + "1");

		TreeSet<String> labels1 = new TreeSet<>();
		labels1.add(INST1_PROP_LABEL_VALUE1);
		labels1.add(INST1_PROP_LABEL_VALUE2);
		testInstance1.addProperty(PROP_LABEL, labels1);
		testInstance1.addProperty(PROP_NUMBER,INST1_PROP_NUMBER_VALUE);

		Instance testInstance2 = new Instance(TEST_INSTANCE + "2");

		TreeSet<String> labels2 = new TreeSet<>();
		labels2.add(INST2_PROP_LABEL_VALUE1);
		labels2.add(INST2_PROP_LABEL_VALUE2);
		testInstance2.addProperty(PROP_LABEL, labels2);
		testInstance2.addProperty(PROP_NUMBER,INST2_PROP_NUMBER_VALUE);

		Instance testInstance3 = new Instance(TEST_INSTANCE + "3");

		TreeSet<String> labels3 = new TreeSet<>();
		labels3.add(INST3_PROP_LABEL_VALUE1);
		labels3.add(INST3_PROP_LABEL_VALUE2);
		testInstance3.addProperty(PROP_LABEL, labels3);
		testInstance3.addProperty(PROP_NUMBER,INST3_PROP_NUMBER_VALUE);

		cache.addInstance(testInstance1);
		cache.addInstance(testInstance2);
		cache.addInstance(testInstance3);
	}
	
	@Test
	public void testgetFunctionId(){
		String[] functionchain = functions.get(PROP_LABEL).get(PROP_RENAMED_LABEL).split("->");
		assertEquals(FUNCTIONID1_EXPECTED, NEWPreprocessor.getFunctionId(functionchain[0]));
		assertEquals(FUNCTIONID2_EXPECTED, NEWPreprocessor.getFunctionId(functionchain[1]));
	}
	
	@Test
	public void testProcess(){
		ACache processedCache = NEWPreprocessor.applyFunctionsToCache(cache, functions);
		assertTrue(processedCache != cache);
		System.out.println(processedCache);

		Iterator<String> i1LabelIterator = processedCache.getInstance(TEST_INSTANCE + "1").getProperty(PROP_RENAMED_LABEL).iterator();
		Iterator<String> i2LabelIterator = processedCache.getInstance(TEST_INSTANCE + "2").getProperty(PROP_RENAMED_LABEL).iterator();
		Iterator<String> i3LabelIterator = processedCache.getInstance(TEST_INSTANCE + "3").getProperty(PROP_RENAMED_LABEL).iterator();

		Iterator<String> i1ConcatIterator = processedCache.getInstance(TEST_INSTANCE + "1").getProperty(PROP_CONCAT).iterator();
		Iterator<String> i2ConcatIterator = processedCache.getInstance(TEST_INSTANCE + "2").getProperty(PROP_CONCAT).iterator();
		Iterator<String> i3ConcatIterator = processedCache.getInstance(TEST_INSTANCE + "3").getProperty(PROP_CONCAT).iterator();

 		assertEquals(INST1_PROP_NUMBER_VALUE_EXPECTED, processedCache.getInstance(TEST_INSTANCE + "1").getProperty(PROP_NUMBER).first());
        assertEquals(INST1_PROP_LABEL_VALUE1_EXPECTED, i1LabelIterator.next());
        assertEquals(INST1_PROP_LABEL_VALUE2_EXPECTED, i1LabelIterator.next());
        assertEquals(INST1_PROP_CONCAT_VALUE1_EXPECTED, i1ConcatIterator.next());
        assertEquals(INST1_PROP_CONCAT_VALUE2_EXPECTED, i1ConcatIterator.next());

        assertEquals(INST2_PROP_NUMBER_VALUE_EXPECTED, processedCache.getInstance(TEST_INSTANCE + "2").getProperty(PROP_NUMBER).first());
        assertEquals(INST2_PROP_LABEL_VALUE1_EXPECTED, i2LabelIterator.next());
        assertEquals(INST2_PROP_LABEL_VALUE2_EXPECTED, i2LabelIterator.next());
        assertEquals(INST2_PROP_CONCAT_VALUE1_EXPECTED, i2ConcatIterator.next());
        assertEquals(INST2_PROP_CONCAT_VALUE2_EXPECTED, i2ConcatIterator.next());

        assertEquals(INST3_PROP_NUMBER_VALUE_EXPECTED, processedCache.getInstance(TEST_INSTANCE + "3").getProperty(PROP_NUMBER).first());
        assertEquals(INST3_PROP_LABEL_VALUE1_EXPECTED, i3LabelIterator.next());
        assertEquals(INST3_PROP_LABEL_VALUE2_EXPECTED, i3LabelIterator.next());
        assertEquals(INST3_PROP_CONCAT_VALUE1_EXPECTED, i3ConcatIterator.next());
        assertEquals(INST3_PROP_CONCAT_VALUE2_EXPECTED, i3ConcatIterator.next());
	}
}
