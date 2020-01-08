package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.engine.partialrecallengine.PartialRecallExecutionEngine;
import org.aksw.limes.core.io.cache.ACache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the execution engine factory class. The execution engine factory
 * class is responsible for choosing and creating the corresponding execution
 * engine object.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class ExecutionEngineFactory {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionEngineFactory.class);

    /**
     * Enum class of allowed execution engine types.
     */
    public enum ExecutionEngineType {
        DEFAULT, SIMPLE, PARTIAL_RECALL
    }

    /**
     * Execution engine factory field for default execution engine.
     */
    public static final String DEFAULT = "default";
    /**
     * Execution engine factory field for simple execution engine.
     */
    public static final String SIMPLE = "simple";

    /**
     * Execution engine factory field for partial recall (LIGER) execution
     * engine.
     */
    public static final String PARTIAL_RECALL = "partial_recall";

    /**
     * Factory function for retrieving an execution engine name from the set of
     * allowed types.
     * 
     * @param name
     *            The name/type of the execution engine.
     * @return a specific execution engine type
     */
    public static ExecutionEngineType getExecutionEngineType(String name) {
        if (name.equalsIgnoreCase(DEFAULT)) {
            return ExecutionEngineType.DEFAULT;
        }
        if (name.equalsIgnoreCase(SIMPLE)) {
            return ExecutionEngineType.SIMPLE;
        }
        if (name.equalsIgnoreCase(PARTIAL_RECALL)) {
            return ExecutionEngineType.PARTIAL_RECALL;
        }
        logger.error(
                "Sorry, " + name + " is not yet implemented. Returning the default execution engine type instead...");
        return ExecutionEngineType.DEFAULT;
    }

    /**
     * Factory function for retrieving the desired execution engine instance.
     * 
     * @param type
     *            Type of the Execution Engine
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Source variable
     * @param targetVar
     *            Target variable
     * @return a specific execution engine instance
     * 
     */
    public static ExecutionEngine getEngine(ExecutionEngineType type, ACache source, ACache target, String sourceVar,
            String targetVar, long maxOpt, double k) {
        switch (type) {
        case DEFAULT:
        case SIMPLE:
            return new SimpleExecutionEngine(source, target, sourceVar, targetVar);
        case PARTIAL_RECALL:
            return new PartialRecallExecutionEngine(source, target, sourceVar, targetVar, maxOpt,k);
        default:
            logger.error(
                    "Sorry, " + type + " is not yet implemented. Returning the default execution engine instead...");
            return new SimpleExecutionEngine(source, target, sourceVar, targetVar);
        }
    }

}
