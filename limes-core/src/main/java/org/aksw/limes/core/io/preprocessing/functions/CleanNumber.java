package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

/**
 * Removes type information from number properties
 * @author Daniel Obraczka
 *
 */
public class CleanNumber extends APreprocessingFunction implements IPreprocessingFunction {
	/**
	 * Matches a number that is followed by "^"
	 */
	public static final Pattern regex = Pattern.compile("[0-9,.-]+(?=\\^)");

	@Override
	public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
			TreeSet<String> oldValues = i.getProperty(property);
			TreeSet<String> newValues = new TreeSet<>();
			for (String value : oldValues) {
				newValues.add(removeTypeInformation(value));
			}
			i.replaceProperty(property, newValues);
		return i;
	}

	/**
	 * Removes type information from number propertyerties, e.g.
	 * "10^^http://www.w3.org/2001/XMLSchema#positiveInteger" would become "10"
	 * 
	 * @param number
	 * @return number without type information as String or 0
	 */
	public static String removeTypeInformation(String number) {
		Matcher m = regex.matcher(number);
		String newValue;
		if (m.find()) {
			newValue = m.group();
			try {
				// Check if it is a parseable double
				Double.parseDouble(newValue);
			} catch (Exception e) {
				return 0 + "";
			}
		} else {
			return 0 + "";
		}
		return newValue;
	}


	@Override
	public int minNumberOfArguments() {
		return 0;
	}

	@Override
	public int maxNumberOfArguments() {
		return 0;
	}

}
