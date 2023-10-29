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
package org.aksw.limes.core.io.preprocessing.functions;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;

import java.util.TreeSet;

public class ToWktPoint extends APreprocessingFunction {

    @Override
    public int minNumberOfArguments() {
        return 2;
    }

    @Override
    public int maxNumberOfArguments() {
        return 2;
    }

    @Override
    public Instance applyFunctionAfterCheck(Instance inst, String property, String... arguments) {
        String latProp = arguments[0].trim();
        String longProp = arguments[1].trim();

        TreeSet<String> newValues = new TreeSet<>();
        for(String latVal : inst.getProperty(latProp)){
            latVal = CleanNumber.removeTypeInformation(latVal);
            for(String longVal : inst.getProperty(longProp)){
                longVal = CleanNumber.removeTypeInformation(longVal);
                newValues.add("POINT(" + latVal + " " + longVal + ")");
            }
        }

        inst.addProperty(property, newValues);
        return inst;
    }

    public boolean isComplex() {
        return true;
    }

}
