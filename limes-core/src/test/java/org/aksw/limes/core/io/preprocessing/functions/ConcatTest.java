package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class ConcatTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";
	public static ArrayList<ArrayList<String>> toConcatInOrder;

	// =============== EXPECTED VALUES ==================================
	public static final TreeSet<String> CONCAT_EXPECTED = new TreeSet<String>(
			Arrays.asList("helloworld", "goodbyeworld"));
	public static final TreeSet<String> CONCAT_EXPECTED_GLUE = new TreeSet<String>(
			Arrays.asList("hello world", "goodbye world"));
	public static final ArrayList<String> CONCAT_IN_ORDER_EXPECTED = new ArrayList<String>(
			Arrays.asList("a1b1c1", "a1b1c2", "a1b2c1", "a1b2c2", "a1b3c1", "a1b3c2", "a2b1c1", "a2b1c2", "a2b2c1",
					"a2b2c2", "a2b3c1", "a2b3c2", "a3b1c1", "a3b1c2", "a3b2c1", "a3b2c2", "a3b3c1", "a3b3c2", "a4b1c1",
					"a4b1c2", "a4b2c1", "a4b2c2", "a4b3c1", "a4b3c2"));

	public static final String[] FUNCTION_CHAIN_1_EXPECTED = new String[] { "label1", "label2" };
	public static final String[] FUNCTION_CHAIN_2_EXPECTED = new String[] { "label1", "label2", "glue=\",\"" };
	public static final String[] FUNCTION_CHAIN_3_EXPECTED = new String[] { "label1", "label2", "glue=\" \"" };

	// =============== PROPERTIES =======================================
	public static final String PROP_CONCAT1 = "concat1";
	public static final String PROP_CONCAT2 = "concat2";
	public static final String PROP_CONCAT_RESULT = "concatresult";

	// =============== VALUES ===========================================
	public static final String PROP_CONCAT1_VALUE1 = "hello";
	public static final String PROP_CONCAT1_VALUE2 = "goodbye";
	public static final String PROP_CONCAT2_VALUE = "world";

	public static final String FUNCTION_CHAIN_1 = "concat(label1,label2)";
	public static final String FUNCTION_CHAIN_2 = "concat(label1,label2,glue=\",\")";
	public static final String FUNCTION_CHAIN_3 = "concat(label1,label2,glue=\" \")";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		TreeSet<String> concat1 = new TreeSet<>();
		concat1.add(PROP_CONCAT1_VALUE1);
		concat1.add(PROP_CONCAT1_VALUE2);
		testInstance.addProperty(PROP_CONCAT1, concat1);
		testInstance.addProperty(PROP_CONCAT2, PROP_CONCAT2_VALUE);

		toConcatInOrder = new ArrayList<>();
		toConcatInOrder.add(new ArrayList<String>(Arrays.asList("a1", "a2", "a3", "a4")));
		toConcatInOrder.add(new ArrayList<String>(Arrays.asList("b1", "b2", "b3")));
		toConcatInOrder.add(new ArrayList<String>(Arrays.asList("c1", "c2")));
	}


	@Test
	public void testConcatElementsInOrder() {
		assertEquals(CONCAT_IN_ORDER_EXPECTED, Concat.concatElementsInOrder(toConcatInOrder));
	}

	@Test
	public void testConcat() throws IllegalNumberOfParametersException {
		new Concat().applyFunction(testInstance, PROP_CONCAT_RESULT, PROP_CONCAT1, PROP_CONCAT2);
		assertEquals(CONCAT_EXPECTED, testInstance.getProperty(PROP_CONCAT_RESULT));
	}

	@Test
	public void testConcatGlue() throws IllegalNumberOfParametersException {
		new Concat().applyFunction(testInstance, PROP_CONCAT_RESULT, PROP_CONCAT1, PROP_CONCAT2, "glue=\" \"");
		assertEquals(CONCAT_EXPECTED_GLUE, testInstance.getProperty(PROP_CONCAT_RESULT));
	}

	@Test
	public void testRetrieveArgumentsConcat() {
		Concat concat = new Concat();
		String[] args1 = concat.retrieveArguments(FUNCTION_CHAIN_1);
		String[] args2 = concat.retrieveArguments(FUNCTION_CHAIN_2);
		String[] args3 = concat.retrieveArguments(FUNCTION_CHAIN_3);
		assertArrayEquals(FUNCTION_CHAIN_1_EXPECTED, args1);
		assertArrayEquals(FUNCTION_CHAIN_2_EXPECTED, args2);
		assertArrayEquals(FUNCTION_CHAIN_3_EXPECTED, args3);
	}
}
