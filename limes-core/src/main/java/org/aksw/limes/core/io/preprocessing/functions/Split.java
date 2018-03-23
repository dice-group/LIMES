package org.aksw.limes.core.io.preprocessing.functions;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.aksw.limes.core.exceptions.MalformedPreprocessingFunctionException;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Split extends APreprocessingFunction {
	Logger logger = LoggerFactory.getLogger(Split.class);
	public static final String SPLIT_CHAR_KEYWORD = "splitChar=";
	public static final String LIMIT_KEYWORD = "limit=";

	@Override
	public int minNumberOfArguments() {
		return 2;
	}

	@Override
	public int maxNumberOfArguments() {
		return -1;
	}

	@Override
	public Instance applyFunctionAfterCheck(Instance inst, String resultProperties, String... arguments) {
		//Get the keyword values
		String possibleKeywordArg1 = arguments[arguments.length - 1];
		String possibleKeywordArg2 = arguments[arguments.length - 2];
		String splitChar = retrieveKeywordArgumentValue(possibleKeywordArg1, SPLIT_CHAR_KEYWORD);
		String property = arguments[0];
		String[] resultPropArr = resultProperties.split(",");
		int limit = resultPropArr.length;
		if (splitChar.equals("")) {
			String limitString = retrieveKeywordArgumentValue(possibleKeywordArg1, LIMIT_KEYWORD).trim();
			// If the last arg is neither limit nor splitChar we assume they are
			// not provided and throw an exception
			if (limitString.equals("")) {
				logger.error("Split character for split function is not provided (empty string is NOT permitted!)");
				throw new MalformedPreprocessingFunctionException();
			}
			limit = Integer.valueOf(limitString);
			// Else we assume splitChar is second to last argument
			splitChar = retrieveKeywordArgumentValue(possibleKeywordArg2, SPLIT_CHAR_KEYWORD);
			// If this is not the case we throw an exception as well
			if (splitChar.equals("")) {
				logger.error("Split character for split function is not provided (empty string is NOT permitted!)");
				throw new MalformedPreprocessingFunctionException();
			}
		}
		//Perfom the split
		for (String toSplit : inst.getProperty(property.trim())) {
			String[] splitArr = toSplit.split(Pattern.quote(splitChar), limit);
			for (int i = 0; i < splitArr.length; i++) {
				inst.addProperty(resultPropArr[i].trim(), splitArr[i]);
			}
		}
		return inst;
	}
}
