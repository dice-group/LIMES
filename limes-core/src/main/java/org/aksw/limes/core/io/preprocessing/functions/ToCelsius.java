package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

public class ToCelsius extends APreprocessingFunction implements IPreprocessingFunction {

	@Override
	public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
		TreeSet<String> oldValues = i.getProperty(property);
		TreeSet<String> newValues = new TreeSet<>();
		for (String value : oldValues) {
			double cleanedValue = Double.parseDouble(CleanNumber.removeTypeInformation(value));
			double result = (cleanedValue - 32) * 5 / 9;
			newValues.add(result + "");
		}
		i.replaceProperty(property, newValues);
		return i;
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
