package org.aksw.limes.core.io.preprocessing;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;

public abstract class APreprocessingFunction implements IPreprocessingFunction {

    public static final String AT               = "@";

    @Override
    public Instance applyFunction(Instance inst, String property, String ... arguments){
		testIfNumberOfArgumentsIsLegal(arguments);
		return applyFunctionAfterCheck(inst, property, arguments);
    }
    
    public abstract Instance applyFunctionAfterCheck(Instance inst, String properties, String ... arguments);

	public void testIfNumberOfArgumentsIsLegal(String... arguments) throws IllegalNumberOfParametersException{
		if(arguments.length < minNumberOfArguments()){
			throw new IllegalNumberOfParametersException("The function " + this.getClass().toString().replace("org.aksw.limes.core.io.preprocessing.functions.","") + " takes at least " + minNumberOfArguments() + " arguments!");
		}
		if(arguments.length > maxNumberOfArguments() && maxNumberOfArguments() != -1){
			throw new IllegalNumberOfParametersException("The function " + this.getClass().toString().replace("org.aksw.limes.core.io.preprocessing.functions.","") + " takes at most " + maxNumberOfArguments() + " arguments!");
		}
	}

	
	/**
	 * Retrieves the arguments for a function, but ignores the function id
	 * @param args the string from the config e.g. <code>replace(test,)</code>
	 * @return array containing only the arguments without function id e.g. <code>[test,]</code>
	 */
	public String[] retrieveArguments(String args) {
		// Remove closing parenthesis and split on opening parenthesis and comma
		// so e.g. "replace(test,)" would become "[replace, test]"
		String[] tmp = args.replace(")", "").split("\\(|,");
		return Arrays.copyOfRange(tmp, 1, tmp.length);
	}
	
}
