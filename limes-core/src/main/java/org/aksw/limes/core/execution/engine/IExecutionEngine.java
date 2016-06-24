package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the engine interface. The engine is responsible for executing the
 * input link specification object.
 *
 * @author Axel-C. Ngonga Ngomo {@literal <}ngonga {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @author Kleanthi Georgala {@literal <}georgala {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @version 1.0
 */
public interface IExecutionEngine {

    /**
     * Implementation of the execution of a link specification.
     *
     * @param spec,
     *            the link specification
     * @param plannerType,
     *            the type of the planner
     * @return The mapping obtained from executing the plan
     */
    AMapping execute(LinkSpecification spec, IPlanner planner);

}
