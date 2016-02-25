package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Implements the engine interface. The engine gets a series of instructions in
 * the form of a plan and runs these instructions and returns a result.
 *
 * @author ngonga
 * @author kleanthi
 */
public interface IExecutionEngine {
    /**
     * Implementation of the execution of a nested plan.
     *
     * @param plan, A nested plan
     * @return The mapping obtained from executing the plan
     */
    Mapping execute(NestedPlan plan);
    
    /**
     * Implementation of the execution of an execution plan.
     *
     * @param plan, An (execution) plan
     * @return The mapping obtained from executing the plan
     */
    Mapping execute(Plan plan);

}
