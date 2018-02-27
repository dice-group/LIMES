package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction;
import org.aksw.limes.core.io.preprocessing.IProcessingFunction;

public class Replace extends AProcessingFunction implements IProcessingFunction {

	@Override
	public Instance applyFunction(Instance i, String[] properties, String... arguments) {
		for(String prop: properties){
            TreeSet<String> oldValues = i.getProperty(prop);
            TreeSet<String> newValues = new TreeSet<>();
            for (String value : oldValues) {
                newValues.add(value.replaceAll(Pattern.quote(arguments[0]),arguments[1]));
            }
            i.replaceProperty(prop, newValues);
		}
		return i;
	
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
		return 2;
	}

	@Override
	public int maxNumberOfArguments() {
		return 2;
	}

}
