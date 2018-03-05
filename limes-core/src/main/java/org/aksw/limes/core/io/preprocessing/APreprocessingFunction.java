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

//	@Override
//	public void process(ACache c, String[] properties, String... arguments) throws IllegalNumberOfParametersException {
//		testIfPropertyNumberIsLegal(properties);
//		testIfNumberOfArgumentsIsLegal(arguments);
//            c.getAllInstances().stream()
//            		//Only get instances with that properties
//                    .filter(APreprocessingFunction.hasProperty(properties))
//                    //Apply function to each instance
//                    .map(instance -> this.applyFunction(instance, properties, arguments)
//                    ).collect(Collectors.toList());
//	}

    @Override
    public Instance applyFunction(Instance inst, String property, String ... arguments){
		testIfNumberOfArgumentsIsLegal(arguments);
		return applyFunctionAfterCheck(inst, property, arguments);
    }
    
    public abstract Instance applyFunctionAfterCheck(Instance inst, String properties, String ... arguments);

	public void testIfNumberOfArgumentsIsLegal(String... arguments) throws IllegalNumberOfParametersException{
		if(arguments.length < minNumberOfArguments()){
			throw new IllegalNumberOfParametersException("The function " + this.getClass().toString().replace("org.aksw.limes.core.io.preprocessing.functions.","") + " takes at least " + minNumberOfArguments() + " number of properties!");
		}
		if(arguments.length > maxNumberOfArguments() && maxNumberOfArguments() != -1){
			throw new IllegalNumberOfParametersException("The function " + this.getClass().toString().replace("org.aksw.limes.core.io.preprocessing.functions.","") + " takes at most " + maxNumberOfArguments() + " number of properties!");
		}
	}

	
	public static Predicate<Instance> hasProperty(String ... property){
		return i -> !Collections.disjoint(i.getAllProperties(), Arrays.asList(property));
	}
	
	
	public String[] retrieveArguments(String args) {
		// Remove closing parenthesis and split on opening parenthesis and comma
		// so e.g. "replace(test,)" would become "[replace, test]"
		String[] tmp = args.replace(")", "").split("\\(|,");
		return Arrays.copyOfRange(tmp, 1, tmp.length);
	}
	
}
