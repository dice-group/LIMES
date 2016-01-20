package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 * @version 2015-11-09
 *
 */
public abstract class MLAlgorithm implements IMLAlgorithm {
	protected Configuration configuration;
	protected Cache sourceCache;
	protected Cache targetCache;
	static Logger logger = Logger.getLogger("LIMES");
//	protected Mapping mapping;

	public MLAlgorithm(Cache sourceCache, Cache targetCache, Configuration configuration) {//, Mapping mapping) {
		super();
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
		this.configuration = configuration;
//		this.mapping = mapping;
	}
	
}
