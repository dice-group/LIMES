package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.result.result.Mapping;
import org.aksw.limes.core.result.result.Result;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions sequentially and returns a mapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public class DefaultExecutionEngine extends ExecutionEngine {

    /**
     * Implementation of the execution of a plan. Instructions of the plan are
     * implemented sequentially.
     *
     * @param plan
     *            An execution plan
     * @return The mapping from running the plan
     */
    public Mapping executePlan(ExecutionPlan plan) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Result execute(Plan plan) {
	// TODO Auto-generated method stub
	return this.executePlan((ExecutionPlan) plan);
    }

}
