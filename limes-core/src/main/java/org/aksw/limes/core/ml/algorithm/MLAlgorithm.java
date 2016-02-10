package org.aksw.limes.core.ml.algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
//	
//	private void setOutStreams(String name) throws FileNotFoundException {
//		File stdFile = new File(name+"_stdOut.txt");
//		PrintStream stdOut = new PrintStream(new FileOutputStream(stdFile, false));
//		File errFile = new File(name+"_errOut.txt");
//		PrintStream errOut = new PrintStream(new FileOutputStream(errFile, false));
//		System.setErr(errOut);
//		System.setOut(stdOut);
//	}
}
