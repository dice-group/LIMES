package org.aksw.limes.core.io.preprocessing.functions;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;

public class RemoveNonAlphanumeric extends APreprocessingFunction{

	@Override
	public int minNumberOfArguments() {
		return 0;
	}

	@Override
	public int maxNumberOfArguments() {
		return 0;
	}

	@Override
	public Instance applyFunctionAfterCheck(Instance inst, String properties, String... arguments) {
		return new RegexReplace().applyFunctionAfterCheck(inst,properties,new String[]{"[^A-Za-z0-9 ]",""});
	}

}
