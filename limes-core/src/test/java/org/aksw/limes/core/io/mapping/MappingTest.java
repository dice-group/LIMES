package org.aksw.limes.core.io.mapping;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class MappingTest {

	@Test
	public void testMappingEquals(){
		AMapping gold = MappingFactory.createDefaultMapping();
		gold.add("Potter","Harry", 0.7);
		gold.add("Granger","Hermione",  0.9);
		HashMap<String,Double> goldMap = new HashMap<String,Double>();
		goldMap.put("Ron", 0.4);
		goldMap.put("Fred", 0.4);
		goldMap.put("George", 0.4);
		gold.add("Weasley", goldMap);
		AMapping test = MappingFactory.createDefaultMapping();
		test.add("Granger","Hermione",  0.9);
		test.add("Potter","Harry", 0.7);
		HashMap<String,Double> testMap = new HashMap<String,Double>();
		testMap.put("Ron", 0.4);
		testMap.put("Fred", 0.4);
		testMap.put("George", 0.4);
		test.add("Weasley", testMap);
		assertEquals(gold,test);
		test.add("Potter","Harry", 0.9);
		assertNotEquals(gold,test);
	}
}
