package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class RemoveBracesTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String REMOVE_BRACES_EXPECTED = "Test";

	// =============== PROPERTIES =======================================
	public static final String PROP_BRACES = "braces";

	// =============== VALUES ===========================================
	public static final String PROP_BRACES_VALUE = "T((e)est";
	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);
		testInstance.addProperty(PROP_BRACES, PROP_BRACES_VALUE);
	}

	@Test
	public void testRemoveBraces() throws IllegalNumberOfParametersException {
		new RemoveBraces().applyFunction(testInstance, PROP_BRACES);
		assertEquals(REMOVE_BRACES_EXPECTED, testInstance.getProperty(PROP_BRACES).first());
	}
}
