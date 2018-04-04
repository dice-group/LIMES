package org.aksw.limes.core.io.preprocessing;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.exceptions.InvalidPreprocessingFunctionException;
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
import org.junit.Test;

public class PreprocessingFunctionFactoryTest {

	@Test
	public void testGetPreprocessingFunctionType(){
		try{
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.CLEAN_IRI) == PreprocessingFunctionType.CLEAN_IRI);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.CLEAN_NUMBER) == PreprocessingFunctionType.CLEAN_NUMBER);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.CONCAT) == PreprocessingFunctionType.CONCAT);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.REGEX_REPLACE) == PreprocessingFunctionType.REGEX_REPLACE);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.REMOVE_LANGUAGE_TAG) == PreprocessingFunctionType.REMOVE_LANGUAGE_TAG);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.RENAME_PROPERTY) == PreprocessingFunctionType.RENAME_PROPERTY);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.REPLACE) == PreprocessingFunctionType.REPLACE);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.TO_CELSIUS) == PreprocessingFunctionType.TO_CELSIUS);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.TO_FAHRENHEIT) == PreprocessingFunctionType.TO_FAHRENHEIT);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.TO_UPPERCASE) == PreprocessingFunctionType.TO_UPPERCASE);
			assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.TO_LOWERCASE) == PreprocessingFunctionType.TO_LOWERCASE);
            assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.REMOVE_BRACES) == PreprocessingFunctionType.REMOVE_BRACES);
            assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.REMOVE_NON_ALPHANUMERIC) == PreprocessingFunctionType.REMOVE_NON_ALPHANUMERIC);
            assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.URI_AS_STRING) == PreprocessingFunctionType.URI_AS_STRING); 
            assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.SPLIT) == PreprocessingFunctionType.SPLIT);
            assertTrue(PreprocessingFunctionFactory.getPreprocessingType(PreprocessingFunctionFactory.TO_WKT_POINT) == PreprocessingFunctionType.TO_WKT_POINT);
		}catch(InvalidPreprocessingFunctionException e){
            e.printStackTrace();
            assertTrue(false);
		}

		try{
			PreprocessingFunctionFactory.getPreprocessingType("failuretest");
            assertTrue(false);
		}catch(InvalidPreprocessingFunctionException e){
		}
		
	}
	
	@Test
	public void testGetPreprocessingFunction(){
		//Assert all values are used
		for(PreprocessingFunctionType p : PreprocessingFunctionType.values()){
			assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(p) != null);
		}
		
		//Assert the correct class is returned
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.CLEAN_IRI) instanceof CleanIri);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.CLEAN_NUMBER) instanceof CleanNumber);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.CONCAT) instanceof Concat);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.REGEX_REPLACE) instanceof RegexReplace);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.REMOVE_LANGUAGE_TAG) instanceof RemoveLanguageTag);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.RENAME_PROPERTY) instanceof RenameProperty);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.REPLACE) instanceof Replace);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.TO_CELSIUS) instanceof ToCelsius);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.TO_FAHRENHEIT) instanceof ToFahrenheit);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.TO_UPPERCASE) instanceof ToUppercase);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.TO_LOWERCASE) instanceof ToLowercase);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.REMOVE_BRACES) instanceof RemoveBraces);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.REMOVE_NON_ALPHANUMERIC) instanceof RemoveNonAlphanumeric);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.URI_AS_STRING) instanceof UriAsString); 
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.SPLIT) instanceof Split);
        assertTrue(PreprocessingFunctionFactory.getPreprocessingFunction(PreprocessingFunctionType.TO_WKT_POINT) instanceof ToWktPoint);
	}
}
