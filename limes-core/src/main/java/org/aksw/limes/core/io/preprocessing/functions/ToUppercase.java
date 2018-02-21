package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;
import java.util.stream.Collectors;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction;
import org.aksw.limes.core.io.preprocessing.IProcessingFunction;

public class ToUppercase extends AProcessingFunction implements IProcessingFunction {

	@Override
	public void applyFunction(ACache c, String... property) throws IllegalNumberOfPropertiesException {
		testIfPropertyNumberIsLegal(property);
		for(String prop: property){
            c.getAllInstances().stream()
            		//Only get instances with that property
                    .filter(instance -> instance.getProperty(prop) != null)
                    .filter(instance -> !instance.getProperty(prop).isEmpty())
                    //Make all values for that property uppercase
                    .map(instance -> {
                        TreeSet<String> oldValues = instance.getProperty(prop);
                        TreeSet<String> newValues = new TreeSet<>();
                        for(String value: oldValues){
                            newValues.add(value.toUpperCase());
                        }
                        instance.replaceProperty(prop, newValues);
                        return instance;
                    }).collect(Collectors.toList());
		}
	}

	@Override
	public int minNumberOfProperties() {
		return 1;
	}

	@Override
	public int maxNumberOfProperties() {
		return -1;
	}

}
