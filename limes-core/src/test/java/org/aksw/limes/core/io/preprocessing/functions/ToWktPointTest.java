package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class ToWktPointTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String TO_WKT_POINT_EXPECTED = "POINT(10 -20)";

	// =============== PROPERTIES =======================================
	public static final String PROP_NUMBER = "number";
	public static final String PROP_TEMPERATURE = "temperature";

	// =============== VALUES ===========================================
	public static final String PROP_NUMBER_VALUE = "10^^http://www.w3.org/2001/XMLSchema#positiveInteger";
	public static final String PROP_NUMBER_VALUE2 = "223";
	public static final String PROP_TEMPERATURE_VALUE = "-20^^http://www.w3.org/2001/XMLSchema#decimal";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		testInstance.addProperty(PROP_NUMBER, PROP_NUMBER_VALUE);
		testInstance.addProperty(PROP_NUMBER, PROP_NUMBER_VALUE2);
		testInstance.addProperty(PROP_TEMPERATURE, PROP_TEMPERATURE_VALUE);
	}

	@Test
	public void testToWktPoint() throws IllegalNumberOfParametersException {
		String propWktPoint = "wktPoint";
		new ToWktPoint().applyFunction(testInstance, propWktPoint, PROP_NUMBER, PROP_TEMPERATURE);
		assertEquals(TO_WKT_POINT_EXPECTED, testInstance.getProperty(propWktPoint).first());
	}
}
