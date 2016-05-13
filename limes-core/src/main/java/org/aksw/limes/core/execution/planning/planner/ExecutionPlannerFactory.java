package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
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
	if (name.equals(DEFAULT)) {
	    return ExecutionPlannerType.DEFAULT;
	}
	if (name.equals(CANONICAL)) {
	    return ExecutionPlannerType.CANONICAL;
	}
	if (name.equals(DYNAMIC)) {
	    return ExecutionPlannerType.DYNAMIC;
	}
	if (name.equals(HELIOS)) {
	    return ExecutionPlannerType.HELIOS;
	}
	logger.error("Sorry, " + name.toString() + " is not yet implemented. Exit with error ...");
	System.exit(1);
	return null;
    }

    /**
     * @param name,
     *            type of the Execution Planner
     * @return a specific execution engine instance
     * @author kleanthi
     */
    public static Planner getPlanner(ExecutionPlannerType type, Cache source, Cache target) {

	if (type == ExecutionPlannerType.DEFAULT)
	    return new CanonicalPlanner();
	if (type == ExecutionPlannerType.HELIOS)
	    return new HeliosPlanner(target, target);
	if (type == ExecutionPlannerType.DYNAMIC)
	 return new DynamicPlanner(source, target);

	logger.error("Sorry, " + type.toString() + " is not yet implemented. Exit with error ...");
	System.exit(1);
	return null;
    }
}
