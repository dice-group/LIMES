package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class RegexReplaceTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String REGEX_REPLACE_EXPECTED = "Ibuprofen is a nonsteroidal anti-inflammatory drug derivative of propionic acid used for relieving pain, helping with fever and reducing inflammation.";

	// =============== PROPERTIES =======================================
	public static final String PROP_ABSTRACT = "dbo:abstract";

	// =============== VALUES ===========================================
	public static final String PROP_ABSTRACT_VALUE = "Ibuprofen (/ˈaɪbjuːproʊfɛn/ or /aɪbjuːˈproʊfən/ EYE-bew-PROH-fən; from isobutylphenylpropanoic acid) is a nonsteroidal anti-inflammatory drug (NSAID) derivative of propionic acid used for relieving pain, helping with fever and reducing inflammation.@en";

	// Used for RegexReplaceTest Removes everything inside braces and language
	// tag
	public static final String REGEX = "\\((.*?)\\) |@\\w*";
	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		testInstance.addProperty(PROP_ABSTRACT, PROP_ABSTRACT_VALUE);
	}

	@Test
	public void testRegexReplace() throws IllegalNumberOfParametersException {
		new RegexReplace().applyFunction(testInstance, PROP_ABSTRACT, REGEX, "");
		assertEquals(REGEX_REPLACE_EXPECTED, testInstance.getProperty(PROP_ABSTRACT).first());
	}
}
