package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

public class RenameProperty extends APreprocessingFunction implements IPreprocessingFunction {

	@Override
	public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
		TreeSet<String> values = i.getProperty(property);
		i.removePropery(property);
		i.addProperty(arguments[0], values);
		return i;
	}

	@Override
	public int minNumberOfArguments() {
		return 1;
	}

	@Override
	public int maxNumberOfArguments() {
		return 1;
	}

}
