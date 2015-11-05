package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.config.LinkSpecification;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;

/**
*
* Implements Planner interface.
* 
* 
* @author ngonga
* @author kleanthi
*/
public interface IPlanner {
    public ExecutionPlan plan(LinkSpecification spec);
}
