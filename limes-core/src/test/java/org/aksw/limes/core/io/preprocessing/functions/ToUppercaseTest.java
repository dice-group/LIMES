package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import java.util.TreeSet;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class ToUppercaseTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String UPPERCASE_EXPECTED = "IBUPROFEN@DE";

	// =============== PROPERTIES =======================================
	public static final String PROP_LABEL = "rdfs:label";

	// =============== VALUES ===========================================
	public static final String PROP_LABEL_VALUE1 = "Ibuprofen@de";
	public static final String PROP_LABEL_VALUE2 = "Ibuprofen@en";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		TreeSet<String> labels = new TreeSet<>();
		labels.add(PROP_LABEL_VALUE1);
		labels.add(PROP_LABEL_VALUE2);
		testInstance.addProperty(PROP_LABEL, labels);
	}

	@Test
	public void testUppercase() throws IllegalNumberOfParametersException {
		new ToUppercase().applyFunction(testInstance, PROP_LABEL);
		assertEquals(UPPERCASE_EXPECTED, testInstance.getProperty(PROP_LABEL).first());
	}
}
