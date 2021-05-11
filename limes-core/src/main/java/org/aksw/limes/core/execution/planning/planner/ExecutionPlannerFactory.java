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
package org.aksw.limes.core.execution.planning.planner;

import org.aksw.limes.core.io.cache.ACache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the planner factory class. The planner factory class is
 * responsible for choosing and creating the corresponding planner object.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class ExecutionPlannerFactory {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionPlannerFactory.class);

    /**
     * Enum class of allowed planner types.
     */
    public enum ExecutionPlannerType {
        DEFAULT, CANONICAL, HELIOS, DYNAMIC;
    }

    /**
     * Planner factory field for default planner.
     */
    public static final String DEFAULT = "default";
    /**
     * Planner factory field for canonical planner.
     */
    public static final String CANONICAL = "canonical";
    /**
     * Planner factory field for helios planner.
     */
    public static final String HELIOS = "helios";
    /**
     * Planner factory field for dynamic planner.
     */
    public static final String DYNAMIC = "dynamic";

    /**
     * Factory function for retrieving a planner name from the set of allowed
     * types.
     *
     * @param name
     *            The name/type of the planner.
     * @return a specific planner type
     */
    public static ExecutionPlannerType getExecutionPlannerType(String name) {
        if (name.equalsIgnoreCase(DEFAULT)) {
            return ExecutionPlannerType.DEFAULT;
        }
        if (name.equalsIgnoreCase(CANONICAL)) {
            return ExecutionPlannerType.CANONICAL;
        }
        if (name.equalsIgnoreCase(DYNAMIC)) {
            return ExecutionPlannerType.DYNAMIC;
        }
        if (name.equalsIgnoreCase(HELIOS)) {
            return ExecutionPlannerType.HELIOS;
        }
        logger.warn("Sorry, " + name + " is not yet implemented. Returning the default planner type instead...");
        return ExecutionPlannerType.HELIOS;
    }

    /**
     * Factory function for retrieving the desired planner instance.
     *
     * @param type
     *            Type of the Planner
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     *
     * @return a specific planner instance
     *
     */
    public static Planner getPlanner(ExecutionPlannerType type, ACache source, ACache target) {

        switch (type) {
            case DEFAULT:
                return new CanonicalPlanner();
            case CANONICAL:
                return new CanonicalPlanner();
            case HELIOS:
                return new HeliosPlanner(source, target);
            case DYNAMIC:
                return new DynamicPlanner(source, target);
            default:
                logger.warn(
                        "Sorry, " + type.toString() + " is not yet implemented. Returning the default planner instead...");
                return new CanonicalPlanner();
        }
    }

}
