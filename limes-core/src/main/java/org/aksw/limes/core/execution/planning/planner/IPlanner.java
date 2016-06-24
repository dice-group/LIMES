package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements the planner interface. It is responsible for generating a plan for
 * an input link specification.
 *
 * @author Axel-C. Ngonga Ngomo {@literal <}ngonga {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @author Kleanthi Georgala {@literal <}georgala {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @version 1.0
 */
public interface IPlanner {
    /**
     * Generates a NestedPlan for a link specification.
     *
     * @param spec,
     *            Input link specification
     * @return NestedPlan of the input link specification
     */
    public NestedPlan plan(LinkSpecification spec);

    /**
     * Returns the status of the planner.
     *
     * @return true if the planner is static or false if it is dynamic
     */
    public boolean isStatic();

    /**
     * Normalization of input link specification.
     *
     * @param spec,
     *            The link specification to normalize
     * @return The normalized link specification
     */
    public LinkSpecification normalize(LinkSpecification spec);
}
