package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the execution engine abstract class. The engine gets as input a
 * link specification and a planner type, executes the plan returned from the
 * planner and returns the set of links as a mapping.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class ExecutionEngine implements IExecutionEngine {
    static Logger logger = LoggerFactory.getLogger(ExecutionEngine.class);
    /**
     * List of intermediate mappings.
     */
    protected List<AMapping> buffer;
    /**
     * Source variable (usually "?x").
     */
    protected String sourceVariable;
    /**
     * Target variable (usually "?y").
     */
    protected String targetVariable;
    /**
     * Source cache.
     */
    protected ACache source;
    /**
     * Target cache.
     */
    protected ACache target;

    protected long optimizationTime = 0l;

    protected double expectedSelectivity = 1.0d;

    /**
     * Constructor for an execution engine.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Source variable
     * @param targetVar
     *            Target variable
     */
    public ExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar) {
        this.setBuffer(new ArrayList<>());
        this.source = source;
        this.target = target;
        this.sourceVariable = sourceVar;
        this.targetVariable = targetVar;
    }

    /**
     * Constructor for an execution engine.
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
    public ExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar, long maxOpt, double k) {
        this.setBuffer(new ArrayList<>());
        this.source = source;
        this.target = target;
        this.sourceVariable = sourceVar;
        this.targetVariable = targetVar;

        if (maxOpt < 0) {
            logger.info("\nOptimization time cannot be negative. Your input value is " + maxOpt
                    + ".\nSetting it to the default value: 0ms.");
            this.optimizationTime = 0l;
        } else
            this.optimizationTime = maxOpt;
        if (k < 0.0 || k > 1.0) {
            logger.info("\nExpected selectivity must be between 0.0 and 1.0. Your input value is " + k
                    + ".\nSetting it to the default value: 1.0.");
            this.expectedSelectivity = 1.0d;
        } else
            this.expectedSelectivity = k;

    }

    public List<AMapping> getBuffer() {
        return buffer;
    }

    public void setBuffer(List<AMapping> buffer) {
        this.buffer = buffer;
    }
}
