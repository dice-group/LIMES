package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.io.cache.Cache;
import org.apache.log4j.Logger;

public class ExecutionPlannerFactory {
    private static final Logger logger = Logger.getLogger(ExecutionEngineFactory.class.getName());

    public enum ExecutionPlannerType {
        DEFAULT, CANONICAL, HELIOS, DYNAMIC;
    }

    public static final String DEFAULT = "default";
    public static final String CANONICAL = "canonical";
    public static final String HELIOS = "helios";
    public static final String DYNAMIC = "dynamic";

    public static ExecutionPlannerType getExecutionPlannerType(String name) {
        if (name.equalsIgnoreCase(DEFAULT)) {
            return ExecutionPlannerType.DEFAULT;
        }
        if (name.equalsIgnoreCase(CANONICAL)) {
            return ExecutionPlannerType.CANONICAL;
        }
        if (name.equalsIgnoreCase(DYNAMIC)) {
            return ExecutionPlannerType.DYNAMIC;
        }
        if (name.equalsIgnoreCase(HELIOS)) {
            return ExecutionPlannerType.HELIOS;
        }
        logger.warn("Sorry, " + name + " is not yet implemented. Returning the default planner type instead...");
        return ExecutionPlannerType.HELIOS;
    }

    /**
     * @param name,
     *            type of the Execution Planner
     * @return a specific execution engine instance
     * @author kleanthi
     */
    public static Planner getPlanner(ExecutionPlannerType type, Cache source, Cache target) {

        switch (type) {
            case DEFAULT:
                return new CanonicalPlanner();
            case CANONICAL:
                return new CanonicalPlanner();
            case HELIOS:
                return new HeliosPlanner(target, target);
            case DYNAMIC:
                return new DynamicPlanner(source, target);
            default:
                logger.warn("Sorry, " + type.toString() + " is not yet implemented. Returning the default planner instead...");
                return new CanonicalPlanner();
        }
    }
}
