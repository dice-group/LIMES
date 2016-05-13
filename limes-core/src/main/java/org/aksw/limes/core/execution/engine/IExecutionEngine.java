package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
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
     * Implementation of the execution of an execution plan.
     *
     * @param source, the source cache
     * @param target, the target cache
     * @param spec, the link specification
     * @param plannerType, the type of the planner
     * 
     * @return The mapping obtained from executing the plan
     */
    Mapping execute(LinkSpecification spec, IPlanner planner);

}
