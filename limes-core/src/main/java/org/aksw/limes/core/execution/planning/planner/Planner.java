package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;

/**
*
* Implements Planner interface.
* 
* 
* @author ngonga
* @author kleanthi
*/
public interface Planner {
    public ExecutionPlan plan(LinkSpec spec);
}
