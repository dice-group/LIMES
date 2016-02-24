package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

/**
 * Implements the default parallel engine class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions in parallel and returns a mapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public class ParallelExecutionEngine extends ExecutionEngine {

    
    public ParallelExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
	super(source, target, sourceVar, targetVar);
	// TODO Auto-generated constructor stub
    }

    /**
     * Implementation of the execution of a nested plan.
     * Instructions of the plan are implemented in parallel.
     *
     * @param plan A nested plan
     * @return The mapping from running the plan
     */
    @Override
    public MemoryMapping execute(NestedPlan plan) {
	throw new UnsupportedOperationException("Not implemented yet");
    }
    /**
     * Implementation of the execution of an execution plan.
     * Instructions of the plan are implemented in parallel.
     *
     * @param plan An execution plan
     * @return The mapping from running the plan
     */
    @Override
    public Mapping execute(Plan plan) {
	throw new UnsupportedOperationException("Not implemented yet");

    }

    

    
}
