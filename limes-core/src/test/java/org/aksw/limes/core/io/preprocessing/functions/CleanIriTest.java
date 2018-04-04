package org.aksw.limes.core.io.preprocessing.functions;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

public class CleanIriTest {

	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String CLEAN_IRI_EXPECTED = "label";

	// =============== PROPERTIES =======================================
	public static final String PROP_IRI = "iri";
	public static final String PROP_IRI_NO_HASHTAG = "irino#";

	// =============== VALUES ===========================================
	public static final String PROP_IRI_VALUE = "http://www.w3.org/2000/01/rdf-schema#label";
	public static final String PROP_IRI_NO_HASHTAG_VALUE = "http://www.w3.org/2000/01/rdf-schema/label";

	public Instance testInstance;

	@Before
	public void prepareData() {
		testInstance = new Instance(TEST_INSTANCE);

		testInstance.addProperty(PROP_IRI, PROP_IRI_VALUE);
		testInstance.addProperty(PROP_IRI_NO_HASHTAG, PROP_IRI_NO_HASHTAG_VALUE);
	}
	@Test
	public void testCleanIri() throws IllegalNumberOfParametersException {
		new CleanIri().applyFunction(testInstance, PROP_IRI);
		assertEquals(CLEAN_IRI_EXPECTED, testInstance.getProperty(PROP_IRI).first());
	}

	@Test
	public void testCleanIriNoHashtag() throws IllegalNumberOfParametersException {
		new CleanIri().applyFunction(testInstance, PROP_IRI_NO_HASHTAG);
		assertEquals(CLEAN_IRI_EXPECTED, testInstance.getProperty(PROP_IRI_NO_HASHTAG).first());
	}
}
