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
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;

import java.util.TreeSet;

/**
 * Deletes everything after and including <code>@</code>
 * Used to delete language tags so e.g. <code>Ibuprofen@en</code> will become <code>Ibuprofen</code>
 *
 * @author Daniel Obraczka
 */
public class RemoveLanguageTag extends APreprocessingFunction implements IPreprocessingFunction {

    @Override
    public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
        TreeSet<String> oldValues = i.getProperty(property);
        TreeSet<String> newValues = new TreeSet<>();
        for (String value : oldValues) {
            if(value.contains(AT)){
                newValues.add(value.substring(0, value.lastIndexOf(AT)));
            }else{
                newValues.add(value);
            }
        }
        i.replaceProperty(property, newValues);
        return i;
    }

    @Override
    public int minNumberOfArguments() {
        return 0;
    }

    @Override
    public int maxNumberOfArguments() {
        return 0;
    }

}
