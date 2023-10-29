/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
