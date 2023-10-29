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

import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets as input a link specification and a planner type, executes the
 * independent parts of the plan returned from the planner in parallel and
 * returns a MemoryMemoryMapping.
 *
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class ParallelExecutionEngine extends ExecutionEngine {
    /**
     * Constructor for a parallel execution engine.
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
    public ParallelExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar, long maxOpt,
                                   double k) {
        super(source, target, sourceVar, targetVar);
    }

    /**
     * Implementation of the execution of an execution plan. Independent parts
     * of the plan are executed in parallel.
     *
     * @param spec
     *            The input link specification
     * @param planner
     *            The chosen planner
     * @return The mapping from running the plan
     */
    @Override
    public AMapping execute(LinkSpecification spec, IPlanner planner) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
