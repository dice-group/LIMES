package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.result.result.Mapping;
import org.aksw.limes.core.result.result.Result;

/**
 * Implements the default parallel engine class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions in parallel and returns a mapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public class ParallelExecutionEngine extends ExecutionEngine {

    
    /**
     * Implementation of the execution of a plan.
     * Instructions of the plan are implemented in parallel.
     *
     * @param plan An execution plan
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
