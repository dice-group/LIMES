package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
*
* Impelements Execution Planner abstact class.
* 
* @author ngonga
* @author kleanthi
*/
public abstract class ExecutionPlanner implements IPlanner{
    public abstract ExecutionPlan plan(LinkSpecification spec);
}
