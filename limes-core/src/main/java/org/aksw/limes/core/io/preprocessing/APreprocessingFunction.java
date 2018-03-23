package org.aksw.limes.core.io.preprocessing;

import java.util.regex.Pattern;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.exceptions.MalformedPreprocessingFunctionException;
import org.aksw.limes.core.io.cache.Instance;

public abstract class APreprocessingFunction implements IPreprocessingFunction {

	public static final String AT = "@";
	/**
	 * Matches commas without preceding equals sign 
	 * Used to split functions that have keywords
	 */
	public static final String commaWithoutPrecedingEquals = "(?<!\\=),";
	
	public static final Pattern checkFunctionString = Pattern.compile("^\\w+\\(.*\\)$|^\\w+$");

	@Override
	public Instance applyFunction(Instance inst, String property, String... arguments) {
		testIfNumberOfArgumentsIsLegal(arguments);
		return applyFunctionAfterCheck(inst, property, arguments);
	}

	public abstract Instance applyFunctionAfterCheck(Instance inst, String property, String... arguments);

	public void testIfNumberOfArgumentsIsLegal(String... arguments) throws IllegalNumberOfParametersException {
		if (arguments.length < minNumberOfArguments()) {
			throw new IllegalNumberOfParametersException("The function "
					+ this.getClass().toString().replace("org.aksw.limes.core.io.preprocessing.functions.", "")
					+ " takes at least " + minNumberOfArguments() + " arguments!");
		}
		if (arguments.length > maxNumberOfArguments() && maxNumberOfArguments() != -1) {
			throw new IllegalNumberOfParametersException("The function "
					+ this.getClass().toString().replace("org.aksw.limes.core.io.preprocessing.functions.", "")
					+ " takes at most " + maxNumberOfArguments() + " arguments!");
		}
	}

	/**
	 * Retrieves the arguments for a function, but ignores the function id
	 * 
	 * @param args
	 *            the string from the config e.g. <code>replace(test,)</code>
	 * @return array containing only the arguments without function id e.g.
	 *         <code>[test,]</code>
	 */
	public String[] retrieveArguments(String args) {
		sanityCheckArguments(args);
		//If sanity check is passed and there is no parenthesis, there are no arguments
		if(!args.contains("(")){
			return new String[]{};
		}
		args = args.substring(args.indexOf("(") + 1,args.length() - 1);
		// This regex is necessesary since keyword values can be ","
		return args.split(commaWithoutPrecedingEquals);
	}
	
	public void sanityCheckArguments(String args) {
		if(!checkFunctionString.matcher(args).find())
			throw new MalformedPreprocessingFunctionException(args);
	}

	public String retrieveKeywordArgumentValue(String arg, String keyword) {
		if(keyword != null && !keyword.equals("") && arg.contains(keyword)){
			return arg.substring(arg.indexOf(keyword) + keyword.length(), arg.length());
		}
		return "";
	}

}
