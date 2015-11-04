package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.cache.Cache;
import org.aksw.limes.core.data.Mapping;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions sequentially and returns a mapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public class DefaultExecutionEngine extends ExecutionEngine {

    public DefaultExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
	super(source, target, sourceVar, targetVar);
	// TODO Auto-generated constructor stub
    }

    /**
     * Implementation of the execution of a plan. Instructions of the plan are
     * implemented sequentially.
     *
     * @param plan
     *            An execution plan
     * @return The mapping from running the plan
     */
    @Override
    public Mapping execute(ExecutionPlan plan) {
	// TODO Auto-generated method stub
	return null;
    }

    

}
