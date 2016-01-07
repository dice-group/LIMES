package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
*
* Implements Planner interface.
* 
* 
* @author ngonga
* @author kleanthi
*/
public interface IPlanner {
    public NestedPlan plan(LinkSpecification spec);
}
