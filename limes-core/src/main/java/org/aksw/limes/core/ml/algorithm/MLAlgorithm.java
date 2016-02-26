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
//	protected Mapping mapping;

	public MLAlgorithm(Cache sourceCache, Cache targetCache, Configuration configuration) {//, Mapping mapping) {
		super();
		this.setSourceCache(sourceCache);
		this.setTargetCache(targetCache);
		this.setConfiguration(configuration);
//		this.mapping = mapping;
	}
//	
//	private void setOutStreams(String name) throws FileNotFoundException {
//		File stdFile = new File(name+"_stdOut.txt");
//		PrintStream stdOut = new PrintStream(new FileOutputStream(stdFile, false));
//		File errFile = new File(name+"_errOut.txt");
//		PrintStream errOut = new PrintStream(new FileOutputStream(errFile, false));
//		System.setErr(errOut);
//		System.setOut(stdOut);
//	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Cache getSourceCache() {
		return sourceCache;
	}

	public void setSourceCache(Cache sourceCache) {
		this.sourceCache = sourceCache;
	}

	public Cache getTargetCache() {
		return targetCache;
	}

	public void setTargetCache(Cache targetCache) {
		this.targetCache = targetCache;
	}
}
