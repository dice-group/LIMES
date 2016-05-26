package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.io.cache.Cache;
import org.apache.log4j.Logger;

public class ExecutionEngineFactory {

    public static final String DEFAULT = "default";

    ;
    public static final String SIMPLE = "simple";
    private static final Logger logger = Logger.getLogger(ExecutionEngineFactory.class.getName());

    public static ExecutionEngineType getExecutionEngineType(String name) {
        if (name.equalsIgnoreCase(DEFAULT)) {
            return ExecutionEngineType.DEFAULT;
        }
        if (name.equalsIgnoreCase(SIMPLE)) {
            return ExecutionEngineType.SIMPLE;
        }
        logger.error("Sorry, " + name + " is not yet implemented. Returning the default execution engine type instead...");
        return ExecutionEngineType.DEFAULT;
    }

    /**
     * @param name,
     *         type of the Execution Engine
     * @param source,
     *         Source cache
     * @param target,
     *         Target cache
     * @param sourceVar,
     *         Source variable (usually "?x")
     * @param targetVar,
     *         Target variable (usually "?y")
     * @return a specific execution engine instance
     * @author kleanthi
     */
    public static ExecutionEngine getEngine(ExecutionEngineType name, Cache source, Cache target, String sourceVar,
                                            String targetVar) {
        switch (name) {
            case DEFAULT:
            case SIMPLE:
                return new SimpleExecutionEngine(source, target, sourceVar, targetVar);
            default:
                logger.error("Sorry, " + name + " is not yet implemented. Returning the default execution engine instead...");
                return new SimpleExecutionEngine(source, target, sourceVar, targetVar);
        }
    }

    public enum ExecutionEngineType {
        DEFAULT, SIMPLE
    }
}
