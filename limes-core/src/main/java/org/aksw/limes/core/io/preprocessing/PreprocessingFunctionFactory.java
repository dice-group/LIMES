package org.aksw.limes.core.io.preprocessing;

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
import org.aksw.limes.core.io.preprocessing.functions.ToCelsius;
import org.aksw.limes.core.io.preprocessing.functions.ToFahrenheit;
import org.aksw.limes.core.io.preprocessing.functions.ToLowercase;
import org.aksw.limes.core.io.preprocessing.functions.ToUppercase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreprocessingFunctionFactory {
    static Logger logger = LoggerFactory.getLogger(PreprocessingFunctionFactory.class);

    public static final String CLEAN_IRI = "cleaniri";
    public static final String CLEAN_NUMBER = "number";
    public static final String CONCAT = "concat";
    public static final String REGEX_REPLACE = "regexreplace";
    public static final String REMOVE_LANGUAGE_TAG = "nolang";
    public static final String RENAME_PROPERTY = "rename";
    public static final String REPLACE = "replace";
    public static final String TO_CELSIUS = "celsius";
    public static final String TO_FAHRENHEIT = "fahrenheit";
    public static final String TO_UPPERCASE = "uppercase";
    public static final String TO_LOWERCASE = "lowercase";
    public static final String REMOVE_BRACES = "removebraces";
    public static final String REMOVE_NON_ALPHANUMERIC = "regularalphabet";
    
    public static PreprocessingFunctionType getPreprocessingType(String expression){
    	switch(expression.trim()){
            case(CLEAN_IRI): 
            	return PreprocessingFunctionType.CLEAN_IRI;
            case(CLEAN_NUMBER): 
            	return PreprocessingFunctionType.CLEAN_NUMBER;
            case(CONCAT): 
            	return PreprocessingFunctionType.CONCAT;
            case(REGEX_REPLACE): 
            	return PreprocessingFunctionType.REGEX_REPLACE;
            case(REMOVE_LANGUAGE_TAG): 
            	return PreprocessingFunctionType.REMOVE_LANGUAGE_TAG;
            case(RENAME_PROPERTY): 
            	return PreprocessingFunctionType.RENAME_PROPERTY;
            case(REPLACE): 
            	return PreprocessingFunctionType.REPLACE;
            case(TO_CELSIUS): 
            	return PreprocessingFunctionType.TO_CELSIUS;
            case(TO_FAHRENHEIT): 
            	return PreprocessingFunctionType.TO_FAHRENHEIT;
            case(TO_UPPERCASE): 
            	return PreprocessingFunctionType.TO_UPPERCASE;
            case(TO_LOWERCASE): 
            	return PreprocessingFunctionType.TO_LOWERCASE;
            case(REMOVE_BRACES): 
            	return PreprocessingFunctionType.REMOVE_BRACES;
            case(REMOVE_NON_ALPHANUMERIC): 
            	return PreprocessingFunctionType.REMOVE_NON_ALPHANUMERIC;
    		default:
    			throw new InvalidPreprocessingFunctionException(expression);
    	}
    }
    
   public static APreprocessingFunction getPreprocessingFunction(PreprocessingFunctionType type){
	    switch(type){
            case CLEAN_IRI: 
            	return new CleanIri();
            case CLEAN_NUMBER: 
            	return new CleanNumber();
            case CONCAT: 
            	return new Concat();
            case REGEX_REPLACE: 
            	return new RegexReplace();
            case REMOVE_LANGUAGE_TAG: 
            	return new RemoveLanguageTag();
            case RENAME_PROPERTY: 
            	return new RenameProperty();
            case REPLACE: 
            	return new Replace();
            case TO_CELSIUS: 
            	return new ToCelsius();
            case TO_FAHRENHEIT: 
            	return new ToFahrenheit();
            case TO_UPPERCASE: 
            	return new ToUppercase();
            case TO_LOWERCASE: 
            	return new ToLowercase();
            case REMOVE_BRACES: 
            	return new RemoveBraces();
            case REMOVE_NON_ALPHANUMERIC: 
            	return new RemoveNonAlphanumeric();
            default:
    			throw new InvalidPreprocessingFunctionException(type.toString());
	    }
   }
    
}
