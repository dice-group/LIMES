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
package org.aksw.limes.core.io.preprocessing;

import org.aksw.limes.core.io.cache.Instance;

public interface IPreprocessingFunction {

    /**
     * Applies the function to this instance
     * @param i the instance that will be preprocessed
     * @param property for unary operators this is the property on which the function will be applied,
     *  for n-ary fucntions this is the name of the new property where the result of the preprocessing will be stored
     * @param arguments some functions take arguments
     * @return the preprocessed instance
     */
    public Instance applyFunction(Instance i, String property, String... arguments);


    public int minNumberOfArguments();

    public int maxNumberOfArguments();

    boolean isComplex();

}
