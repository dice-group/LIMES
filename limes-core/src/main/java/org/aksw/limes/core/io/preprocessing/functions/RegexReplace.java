package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

public class RegexReplace extends APreprocessingFunction implements IPreprocessingFunction {

	@Override
	public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
		TreeSet<String> oldValues = i.getProperty(property);
		TreeSet<String> newValues = new TreeSet<>();
		for (String value : oldValues) {
			newValues.add(value.replaceAll(arguments[0], arguments[1]));
		}
		i.replaceProperty(property, newValues);
		return i;

	}

	@Override
	public int minNumberOfArguments() {
		return 2;
	}

	@Override
	public int maxNumberOfArguments() {
		return 2;
	}

}
