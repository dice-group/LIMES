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
package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.io.cache.Instance;

/**
 * Implements the measure interface.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IMeasure {
    /**
     * Returns the similarity between two objects.
     *
     * @param object1,
     *            the source object
     * @param object2,
     *            the target object
     *
     * @return The similarity of the objects
     */
    public double getSimilarity(Object object1, Object object2);

    /**
     * Returns the similarity between two instances, given their corresponding
     * properties.
     *
     * @param instance1,
     *            the source instance
     * @param instance2,
     *            the target instance
     * @param property1,
     *            the source property
     * @param property2,
     *            the target property
     * @return The similarity of the instances
     */
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2);

    /**
     * Returns the runtime approximation of a measure.
     *
     * @param mappingSize,
     *            the mapping size returned by the measure
     *
     * @return The runtime of the measure
     */
    public double getRuntimeApproximation(double mappingSize);

    /**
     * Returns name of a measure.
     *
     *
     * @return Measure name as a string
     */
    public String getName();

    /**
     * Returns type of a measure.
     *
     *
     * @return The runtime of the measure
     */
    public String getType();

}
