package org.aksw.limes.core.io.preprocessing.functions;

import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

/**
 * Cleans IRIs e.g. <code>http://www.w3.org/2000/01/rdf-schema#label</code> will
 * become <code>label</code> Basically it removes everything until the last
 * <code>/</code> or <code>#</code>
 * 
 * @author Daniel Obraczka
 *
 */
public class CleanIri extends APreprocessingFunction implements IPreprocessingFunction {

	@Override
	public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
		TreeSet<String> oldValues = i.getProperty(property);
		TreeSet<String> newValues = new TreeSet<>();
		for (String value : oldValues) {
			newValues.add(cleanIriString(value));
		}
		i.replaceProperty(property, newValues);
		return i;
	}

	public static String cleanIriString(String iri) {
		if (iri.contains("#")) {
			return iri.substring(iri.indexOf("#") + 1);
		} else if (iri.contains("/")) {
			return iri.substring(iri.lastIndexOf("/") + 1);
		}
		return iri;
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
