package org.aksw.limes.core.io.preprocessing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.exceptions.MalformedPreprocessingFunctionException;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.functions.CleanIri;
import org.aksw.limes.core.io.preprocessing.functions.CleanNumber;
import org.aksw.limes.core.io.preprocessing.functions.Concat;
import org.aksw.limes.core.io.preprocessing.functions.RegexReplace;
import org.aksw.limes.core.io.preprocessing.functions.RemoveBraces;
import org.aksw.limes.core.io.preprocessing.functions.RemoveLanguageTag;
import org.aksw.limes.core.io.preprocessing.functions.RemoveNonAlphanumeric;
import org.aksw.limes.core.io.preprocessing.functions.RenameProperty;
import org.aksw.limes.core.io.preprocessing.functions.Replace;
import org.aksw.limes.core.io.preprocessing.functions.Split;
import org.aksw.limes.core.io.preprocessing.functions.ToCelsius;
import org.aksw.limes.core.io.preprocessing.functions.ToFahrenheit;
import org.aksw.limes.core.io.preprocessing.functions.ToLowercase;
import org.aksw.limes.core.io.preprocessing.functions.ToUppercase;
import org.aksw.limes.core.io.preprocessing.functions.ToWktPoint;
import org.aksw.limes.core.io.preprocessing.functions.UriAsString;
import org.junit.Before;
import org.junit.Test;

public class PreprocessingFunctionsTest {
	public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

	// =============== EXPECTED VALUES ==================================
	public static final String[] FUNCTION_CHAIN_1_EXPECTED = new String[] { "label1", "label2" };
	public static final String[] FUNCTION_CHAIN_2_EXPECTED = new String[] { "label1", "label2", "glue=\",\"" };
	public static final String[] FUNCTION_CHAIN_3_EXPECTED = new String[] { "label1", "label2", "glue=\" \"" };
	public static final String[] FUNCTION_CHAIN_4_EXPECTED = new String[] {};
	public static final String[] FUNCTION_CHAIN_5_EXPECTED = new String[] { "rdfs:label", "splitChar=\",\"" };
	public static final String[] FUNCTION_CHAIN_6_EXPECTED = new String[] { "rdfs:label", "splitChar=\"\"\"" };
	public static final String KEYWORD_RETRIEVAL_EXPECTED1 = ",";
	public static final String KEYWORD_RETRIEVAL_EXPECTED2 = " ";

	// =============== PROPERTIES =======================================
	public static final String PROP_LABEL = "rdfs:label";

	// =============== VALUES ===========================================
	public static final String PROP_LABEL_VALUE1 = "Ibuprofen@de";
	public static final String PROP_LABEL_VALUE2 = "Ibuprofen@en";

	public static final String FUNCTION_CHAIN_1 = "concat(label1,label2)";
	public static final String FUNCTION_CHAIN_2 = "concat(label1,label2,glue=\",\")";
	public static final String FUNCTION_CHAIN_3 = "concat(label1,label2,glue=\" \")";
	public static final String FUNCTION_CHAIN_4 = "lowercase";
	public static final String FUNCTION_CHAIN_5 = "split(rdfs:label, splitChar=\",\")";
	public static final String FUNCTION_CHAIN_6 = "split(rdfs:label, splitChar=\"\"\")";

	public static final String KEYWORD1 = "glue=\",\"";
	public static final String KEYWORD2 = "glue=\" \"";

	public static final String SANITY_CHECK1 = "concat(label1,label2,glue=\",\"";
	public static final String SANITY_CHECK2 = "concatlabel1,label2,glue=\",\")";
	public static final String SANITY_CHECK3 = "(label1,label2,glue=\",\")";
	public static final String SANITY_CHECK4 = "concat(label1,label2,glue=\",)";
	public static final String SANITY_CHECK5 = "split(label1, limit=\"1, splitChar=\"\"\")";

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
	public void testAProcessingFunctionWrongArgumentNumber() {
		try {
			// The relevant functionality is the same for all classes that
			// extend AProcessingFunction
			new ToUppercase().applyFunction(testInstance, PROP_LABEL, "bla");
			assertFalse(true);
		} catch (IllegalNumberOfParametersException e) {
		}
	}

	@Test
	public void testRetrieveArguments() {
		String[] args4 = new ToLowercase().retrieveArguments(FUNCTION_CHAIN_4);
		assertArrayEquals(FUNCTION_CHAIN_4_EXPECTED, args4);
		String[] args5 = new Split().retrieveArguments(FUNCTION_CHAIN_5);
		assertArrayEquals(FUNCTION_CHAIN_5_EXPECTED, args5);
		String[] args6 = new Split().retrieveArguments(FUNCTION_CHAIN_6);
		assertArrayEquals(FUNCTION_CHAIN_6_EXPECTED, args6);
	}

	@Test
	public void testSanityCheckArguments() {
		try {
			new Split().sanityCheckArguments(SANITY_CHECK1);
			assertTrue(false);
		} catch (MalformedPreprocessingFunctionException e1) {
			try {
				new Split().sanityCheckArguments(SANITY_CHECK2);
				assertTrue(false);
			} catch (MalformedPreprocessingFunctionException e2) {
				try {
					new Split().sanityCheckArguments(SANITY_CHECK3);
					assertTrue(false);
				} catch (MalformedPreprocessingFunctionException e3) {
					try {
						new Split().sanityCheckArguments(SANITY_CHECK4);
						assertTrue(false);
					} catch (MalformedPreprocessingFunctionException e4) {
						try {
							new Split().sanityCheckArguments(SANITY_CHECK5);
							assertTrue(false);
						} catch (MalformedPreprocessingFunctionException e5) {
							try {
								new Split().sanityCheckArguments(FUNCTION_CHAIN_1);
								new Split().sanityCheckArguments(FUNCTION_CHAIN_2);
								new Split().sanityCheckArguments(FUNCTION_CHAIN_3);
								new Split().sanityCheckArguments(FUNCTION_CHAIN_4);
							} catch (MalformedPreprocessingFunctionException e6) {
								assertTrue(false);
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testRetrieveKeywordArguments() {
		String keyword1 = new Concat().retrieveKeywordArgumentValue(KEYWORD1, Concat.GLUE_KEYWORD);
		assertEquals(KEYWORD_RETRIEVAL_EXPECTED1, keyword1);
		String keyword2 = new Concat().retrieveKeywordArgumentValue(KEYWORD2, Concat.GLUE_KEYWORD);
		assertEquals(KEYWORD_RETRIEVAL_EXPECTED2, keyword2);
		String keyword3 = new Split().retrieveKeywordArgumentValue(FUNCTION_CHAIN_1, Split.SPLIT_CHAR_KEYWORD);
		assertEquals("", keyword3);
		try{
            new Split().retrieveKeywordArgumentValue("splitChar=,", Split.SPLIT_CHAR_KEYWORD);
            assertTrue(false);
        } catch (MalformedPreprocessingFunctionException e6) {
        }
	}
}
