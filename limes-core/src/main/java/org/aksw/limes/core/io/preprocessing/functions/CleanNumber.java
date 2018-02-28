package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction;
import org.aksw.limes.core.io.preprocessing.IProcessingFunction;

/**
 * Removes type information from number properties
 * 
 * @author Daniel Obraczka
 *
 */
public class CleanNumber extends AProcessingFunction implements IProcessingFunction {
	/**
	 * Matches a number that is followed by "^"
	 */
	public static final Pattern regex = Pattern.compile("[0-9,.-]+(?=\\^)");

	@Override
	public Instance applyFunction(Instance i, String[] properties, String... arguments) {
		for (String prop : properties) {
			TreeSet<String> oldValues = i.getProperty(prop);
			TreeSet<String> newValues = new TreeSet<>();
			for (String value : oldValues) {
				newValues.add(removeTypeInformation(value));
			}
			i.replaceProperty(prop, newValues);
		}
		return i;

	}

	/**
	 * Removes type information from number properties, e.g.
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
	public int minNumberOfProperties() {
		return 1;
	}

	@Override
	public int maxNumberOfProperties() {
		return -1;
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
