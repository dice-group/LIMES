package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

/**
 * Implements the engine interface. The engine gets a series of instructions in
 * the form of a plan and runs these instructions and returns a result.
 *
 * @author ngonga
 * @author kleanthi
 */
public interface IEngine {
    /**
     * Implementation of the execution of a plan.
     *
     * @param plan
     *            An execution plan
     * @return The result mapping from running the plan
     */
    Mapping execute(ExecutionPlan plan);

}
