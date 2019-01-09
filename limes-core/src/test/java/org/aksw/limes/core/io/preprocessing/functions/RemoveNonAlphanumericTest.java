package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class RemoveNonAlphanumericTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String REMOVE_NON_ALPHANUMERIC_EXPECTED = "alphanumeric";

	// =============== PROPERTIES =======================================
	public static final String PROP_NON_ALPHANUMERIC = "nonalphanumeric";

	// =============== VALUES ===========================================
	public static final String PROP_NON_ALPHANUMERIC_VALUE = "a!lp%h|a^n&u*m<e)r=ic";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		testInstance.addProperty(PROP_NON_ALPHANUMERIC, PROP_NON_ALPHANUMERIC_VALUE);
	}

	@Test
	public void testRemoveNonAlphanumeric() throws IllegalNumberOfParametersException {
		new RemoveNonAlphanumeric().applyFunction(testInstance, PROP_NON_ALPHANUMERIC);
		assertEquals(REMOVE_NON_ALPHANUMERIC_EXPECTED, testInstance.getProperty(PROP_NON_ALPHANUMERIC).first());
	}
}
