package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class UriAsStringTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String URI_AS_STRING_EXPECTED = "uri as string";

	// =============== PROPERTIES =======================================
	public static final String PROP_URI_AS_STRING = "uriasstring";

	// =============== VALUES ===========================================
	public static final String PROP_URI_AS_STRING_VALUE = "http://example.org/uri_as_string";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);
		testInstance.addProperty(PROP_URI_AS_STRING, PROP_URI_AS_STRING_VALUE);
	}

	@Test
	public void testUriAsString() throws IllegalNumberOfParametersException {
		new UriAsString().applyFunction(testInstance, PROP_URI_AS_STRING);
		assertEquals(URI_AS_STRING_EXPECTED, testInstance.getProperty(PROP_URI_AS_STRING).first());
	}
}
