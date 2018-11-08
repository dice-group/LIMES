package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class ToFahrenheitTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String TO_FAHRENHEIT_EXPECTED = "-4.0";

	// =============== PROPERTIES =======================================
	public static final String PROP_TEMPERATURE = "temperature";

	// =============== VALUES ===========================================
	public static final String PROP_TEMPERATURE_VALUE = "-20^^http://www.w3.org/2001/XMLSchema#decimal";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		testInstance.addProperty(PROP_TEMPERATURE, PROP_TEMPERATURE_VALUE);
	}

	@Test
	public void testToFahrenheit() throws IllegalNumberOfParametersException {
		new ToFahrenheit().applyFunction(testInstance, PROP_TEMPERATURE);
		assertEquals(TO_FAHRENHEIT_EXPECTED, testInstance.getProperty(PROP_TEMPERATURE).first());
	}
}
