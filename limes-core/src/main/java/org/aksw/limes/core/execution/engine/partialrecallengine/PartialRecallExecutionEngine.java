package org.aksw.limes.core.execution.engine.partialrecallengine;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.engine.partialrecallengine.refinement.LigerRefinementOperator;
import org.aksw.limes.core.execution.engine.partialrecallengine.refinement.PartialRecallRefinementOperator;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.ExtendedLinkSpecification;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the partial recall execution engine class. The idea is that the
 * engine gets as input a link specification and a planner type, finds a
 * subsumed link specification that achieves the lowest expected run time while
 * achieving at least a predefined excepted recall. Then the partial recall
 * execution engine executes the independent parts of the plan returned from the
 * planner sequentially and returns a mapping.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class PartialRecallExecutionEngine extends SimpleExecutionEngine {

    static Logger logger = LoggerFactory.getLogger(PartialRecallExecutionEngine.class);

    /**
     * Constructor for the partial recall execution engine.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Source variable
     * @param targetVar
     *            Target variable
     * @param maxOpt,
     *            optimization time constraint
     * @param k,
     *            expected selectivity
     */
    public PartialRecallExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar, long maxOpt,
            double k) {
        //@todo: @Kleanthi please review
        //super(source, target, sourceVar, targetVar, maxOpt, k);
        super(source, target, sourceVar, targetVar);
    }

    @Override
    public AMapping execute(LinkSpecification spec, IPlanner planner) {

        // normalization is necessary cause liger's refinement operator is
        // not defined for XOR
        spec = new ExtendedLinkSpecification(spec.getFullExpression(), spec.getThreshold());

        PartialRecallRefinementOperator liger = new LigerRefinementOperator(source, target, expectedSelectivity,
                optimizationTime, spec);
        liger.optimize();
        LinkSpecification newSpec = liger.getBest().getLinkSpecification();
        // needed in case of dynamic planner
        // its normalize function initiliazes important structures
        return super.execute(newSpec, planner);
    }

}
