package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
*
* Impelements Execution Planner abstract class.
* 
* @author ngonga
* @author kleanthi
*/
public abstract class Planner implements IPlanner{
    /**
     * Generates a NestedPlan for a link specification
     *
     * @param spec
     *            Input link specification
     * @return NestedPlan of the input link specification
     */
    public abstract NestedPlan plan(LinkSpecification spec);
}
