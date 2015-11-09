package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-09
 *
 */
public abstract class MLAlgorithm implements IMLAlgorithm {
	
	protected Cache sourceCache;
	protected Cache targetCache;
	protected Mapping mapping;

	public MLAlgorithm(Cache sourceCache, Cache targetCache, Mapping mapping) {
		super();
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
		this.mapping = mapping;
	}
	
}
