package org.aksw.limes.core.io.preprocessing;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PreprocessorTest {

    private static final String TEST_URI = "http://dbpedia.org/resource/Category:Random_House_books";

    @Test
    public void testUriString() {
        String anObject = "Random House books";
        String process = Preprocessor.process(TEST_URI, Preprocessor.URI_AS_STRING);
        assertTrue(process.equals(anObject));
    }

    @Test
    public void testLowerCase() {
        String anObject = "http://dbpedia.org/resource/category:random_house_books";
        String process = Preprocessor.process(TEST_URI, Preprocessor.LOWER_CASE);
        assertTrue(process.equals(anObject));
    }

    @Test
    public void testUpperCase() {
        String anObject = "HTTP://DBPEDIA.ORG/RESOURCE/CATEGORY:RANDOM_HOUSE_BOOKS";
        String process = Preprocessor.process(TEST_URI, Preprocessor.UPPER_CASE);
        assertTrue(process.equals(anObject));
    }

    @Test
    public void testReplace() {
        String anObject = "http://dbpedia.org/resource/:Random_House_books";
        String process = Preprocessor.process(TEST_URI, Preprocessor.REPLACE + "(Category,)");
        assertTrue(process.equals(anObject));
    }


}
