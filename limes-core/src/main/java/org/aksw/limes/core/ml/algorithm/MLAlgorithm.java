package org.aksw.limes.core.ml.algorithm;


import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
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

	/**
	 * @param sourceCache
	 * @param targetCache
	 * @param configuration
	 */
	protected MLAlgorithm(Cache sourceCache, Cache targetCache, Configuration configuration) {//, Mapping mapping) {
		super();
		this.setSourceCache(sourceCache);
		this.setTargetCache(targetCache);
		this.setConfiguration(configuration);
	}

	/**
	 * @return the LIMES configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the LIMES configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the source dataset cache
	 */
	public Cache getSourceCache() {
		return sourceCache;
	}

	/**
	 * @param sourceCache the source dataset cache
	 */
	public void setSourceCache(Cache sourceCache) {
		this.sourceCache = sourceCache;
	}

	/**
	 * @return the target dataset cache
	 */
	public Cache getTargetCache() {
		return targetCache;
	}

	/**
	 * @param targetCache the target dataset cache
	 */
	public void setTargetCache(Cache targetCache) {
		this.targetCache = targetCache;
	}
}
