package org.aksw.limes.core.io.preprocessing;

import java.util.regex.Pattern;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.exceptions.MalformedPreprocessingFunctionException;
import org.aksw.limes.core.io.cache.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class APreprocessingFunction implements IPreprocessingFunction {
	static Logger logger = LoggerFactory.getLogger(APreprocessingFunction.class.getName());

	public static final String AT = "@";
	/**
	 * Matches comma if that comma is not followed by quotation mark
	 */
	public static final String commaNotInsideQuotation = "," //match comma
														+"(?!\")"; //negative lookahead checks if comma is followed by quotation
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
		String[] argumentsSplit = args.split(commaNotInsideQuotation);
		for(int i = 0; i < argumentsSplit.length; i++){
			//Remove preceding whitespace
			argumentsSplit[i] = argumentsSplit[i].replaceAll("^\\s+","");
		}
		return argumentsSplit;
	}
	
	public void sanityCheckArguments(String args) {
		//Check if there is an even amount of quotation marks or if quotations marks are used as arguments
		if((args.length() - args.replace("\"","").length()) % 2 != 0 || args.contains("\"\"\"")){
			//Check if the quotations that are not arguments are correct
			String permittedUnevenQuotationsRemoved = args.replace("\"\"\"","");
			if((permittedUnevenQuotationsRemoved.length() - permittedUnevenQuotationsRemoved.replace("\"","").length()) % 2 != 0){
                logger.error("Unmatched quotation mark!");
                throw new MalformedPreprocessingFunctionException(args);
			}
		}
		if(!checkFunctionString.matcher(args).find()){
			throw new MalformedPreprocessingFunctionException(args);
		}
	}

	public String retrieveKeywordArgumentValue(String arg, String keyword) {
		if(keyword != null && !keyword.equals("") && arg.contains(keyword)){
			String afterKeyword = arg.substring(arg.indexOf(keyword) + keyword.length(), arg.length());
			if(!afterKeyword.startsWith("\"") || !afterKeyword.endsWith("\"")){
				logger.error("Keyword values must be enclosed in quotation marks!");
                throw new MalformedPreprocessingFunctionException(arg);
			}
			return afterKeyword.substring(1, afterKeyword.length() - 1);
		}
		return "";
	}

	public boolean isComplex() {
	    return false;
    }

}
