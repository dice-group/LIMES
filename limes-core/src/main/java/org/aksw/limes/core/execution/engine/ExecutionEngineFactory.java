package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.io.cache.Cache;
import org.apache.log4j.Logger;

public class ExecutionEngineFactory {
    public static final String DEFAULT = "default";
    public static final String PARALLEL = "parallel";
    private static final Logger logger = Logger.getLogger(ExecutionEngineFactory.class.getName());

    /**
     * @param name,
     *            type of the Execution Engine
     * @return a specific execution engine instance
     * @author kleanthi
     */
    public static ExecutionEngine getEngine(String name, Cache source, Cache target, String sourceVar, String targetVar) {

	if (name.equalsIgnoreCase(DEFAULT))
	    return new SimpleExecutionEngine(source, target, sourceVar, targetVar);
	if (name.equalsIgnoreCase(PARALLEL))
	    return new ParallelExecutionEngine(source, target, sourceVar, targetVar);

	logger.error("Sorry, " + name + " is not yet implemented. Exit with error ...");
	System.exit(1);
	return null;
    }
}
