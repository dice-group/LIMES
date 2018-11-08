package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

public class Replace extends APreprocessingFunction implements IPreprocessingFunction {

	@Override
	public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
		// If no replacee is provided we provide the empty string
		String replacee;
		if (arguments.length == 1) {
			replacee = "";
		} else {
			replacee = arguments[1];
		}

		TreeSet<String> oldValues = i.getProperty(property);
		TreeSet<String> newValues = new TreeSet<>();
		for (String value : oldValues) {
			newValues.add(value.replaceAll(Pattern.quote(arguments[0]), replacee));
		}
		i.replaceProperty(property, newValues);
		return i;

	}

	public int minNumberOfArguments() {
		return 1;
	}

	@Override
	public int maxNumberOfArguments() {
		return 2;
	}

}
