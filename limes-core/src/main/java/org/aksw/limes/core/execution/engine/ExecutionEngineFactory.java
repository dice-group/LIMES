package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.io.cache.Cache;
import org.apache.log4j.Logger;

public class ExecutionEngineFactory {

	public enum ExecutionEngineType {
		DEFAULT, 
		PARALLEL
	};

	private static final Logger logger = Logger.getLogger(ExecutionEngineFactory.class.getName());

	/**
	 * @param name, type of the Execution Engine
	 * @param source, Source cache
	 * @param target, Target cache
	 * @param sourceVar, Source variable (usually "?x")
	 * @param targetVar, Target variable (usually "?y")
	 *            
	 * @return a specific execution engine instance
	 * 
	 * @author kleanthi
	 */
	public static ExecutionEngine getEngine(ExecutionEngineType name, Cache source, Cache target, String sourceVar,
			String targetVar) {

		if (name == ExecutionEngineType.DEFAULT)
			return new SimpleExecutionEngine(source, target, sourceVar, targetVar);
		//if (name.equalsIgnoreCase(PARALLEL))
		//    return new ParallelExecutionEngine(source, target, sourceVar, targetVar);

		logger.error("Sorry, " + name + " is not yet implemented. Exit with error ...");
		System.exit(1);
		return null;
	}
}
