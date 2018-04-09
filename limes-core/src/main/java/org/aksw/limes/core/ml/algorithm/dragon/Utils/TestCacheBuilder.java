package org.aksw.limes.core.ml.algorithm.dragon.Utils;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

public class TestCacheBuilder {
	

	/**
	 * Fills testSource and testTarget with elements from originalSource and originalTarget that are contained in m
	 * @param m ReferenceMapping
	 * @param originalSource full source cache
	 * @param originalTarget full target cache
	 * @param testSource sourceCache to be filled
	 * @param testTarget targetCache to be filled
	 */
	public static void buildFromMapping(AMapping m, ACache originalSource, ACache originalTarget, ACache testSource, ACache testTarget){
		if (m == null || originalSource == null || originalTarget == null || testSource == null || testTarget == null)
			return;
		for (String s : m.getMap().keySet()) {
			testSource.addInstance(originalSource.getInstance(s).copy());
			for (String t : m.getMap().get(s).keySet()) {
				testTarget.addInstance(originalTarget.getInstance(t).copy());
			}
		}
	}
}
