package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;

public class ToWktPoint extends APreprocessingFunction {

	@Override
	public int minNumberOfArguments() {
		return 2;
	}

	@Override
	public int maxNumberOfArguments() {
		return 2;
	}

	@Override
	public Instance applyFunctionAfterCheck(Instance inst, String property, String... arguments) {
		String latProp = arguments[0].trim();
		String longProp = arguments[1].trim();

		TreeSet<String> newValues = new TreeSet<>();
		for(String latVal : inst.getProperty(latProp)){
			latVal = CleanNumber.removeTypeInformation(latVal);
			for(String longVal : inst.getProperty(longProp)){
    			longVal = CleanNumber.removeTypeInformation(longVal);
                newValues.add("POINT(" + latVal + " " + longVal + ")");
			}
		}

		inst.addProperty(property, newValues);
		return inst;
	}

    public boolean isComplex() {
        return true;
    }

}
