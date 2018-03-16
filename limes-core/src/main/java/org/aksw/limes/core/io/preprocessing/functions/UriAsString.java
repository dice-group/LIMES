package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;

public class UriAsString extends APreprocessingFunction {

	@Override
	public int minNumberOfArguments() {
		return 0;
	}

	@Override
	public int maxNumberOfArguments() {
		return 0;
	}

	@Override
	public Instance applyFunctionAfterCheck(Instance inst, String property, String... arguments) {
		TreeSet<String> oldValues = inst.getProperty(property);
		TreeSet<String> newValues = new TreeSet<>();
		for (String value : oldValues) {
			newValues.add(CleanIri.cleanIriString(value).replaceAll("_"," "));
		}
		inst.replaceProperty(property, newValues);
		return inst;
	}

}
