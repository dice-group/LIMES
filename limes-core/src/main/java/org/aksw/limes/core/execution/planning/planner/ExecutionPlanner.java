package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;

/**
*
* Impelements Execution Planner abstact class.
* 
* @author ngonga
* @author kleanthi
*/
public abstract class ExecutionPlanner implements Planner{
    public abstract ExecutionPlan plan(LinkSpec spec);
}
