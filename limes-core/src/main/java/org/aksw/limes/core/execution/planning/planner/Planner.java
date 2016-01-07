package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
*
* Impelements Execution Planner abstact class.
* 
* @author ngonga
* @author kleanthi
*/
public abstract class Planner implements IPlanner{
    public abstract NestedPlan plan(LinkSpecification spec);
}
