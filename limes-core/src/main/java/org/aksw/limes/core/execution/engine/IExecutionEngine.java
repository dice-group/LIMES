package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the engine interface. The engine is responsible for executing the
 * input link specification object.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IExecutionEngine {

    /**
     * Implementation of the execution of a link specification.
     *
     * @param spec
     *            The link specification
     * @param planner,
     *            The type of the planner
     * @return The mapping obtained from executing the plan
     */
    AMapping execute(LinkSpecification spec, IPlanner planner);

}
