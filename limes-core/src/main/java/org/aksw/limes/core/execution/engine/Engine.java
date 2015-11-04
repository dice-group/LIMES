package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.mapping.MemoryMapping;

/**
 * Implements the engine interface. The engine gets a series of instructions in
 * the form of a plan and runs these instructions and returns a result.
 *
 * @author ngonga
 * @author kleanthi
 */
public interface Engine {
    /**
     * Implementation of the execution of a plan.
     *
     * @param plan
     *            An execution plan
     * @return The result mapping from running the plan
     */
    MemoryMapping execute(ExecutionPlan plan);

}
