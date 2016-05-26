package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements Planner interface.
 *
 * @author ngonga
 * @author kleanthi
 */
public interface IPlanner {
    /**
     * Generates a NestedPlan for a link specification
     *
     * @param spec
     *         Input link specification
     * @return NestedPlan of the input link specification
     */
    public NestedPlan plan(LinkSpecification spec);

    /**
     * Returns the nature of the planner
     *
     * @return true if the planner is static or false if it is static
     */
    public boolean isStatic();

    /**
     * Normalization of input link specification
     *
     * @param spec,
     *         the input link specification
     */
    public LinkSpecification normalize(LinkSpecification spec);
}
