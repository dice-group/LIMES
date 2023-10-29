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

import org.aksw.limes.core.exceptions.MalformedPreprocessingFunctionException;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class Split extends APreprocessingFunction {
    Logger logger = LoggerFactory.getLogger(Split.class);
    public static final String SPLIT_CHAR_KEYWORD = "splitChar=";

    @Override
    public int minNumberOfArguments() {
        return 2;
    }

    @Override
    public int maxNumberOfArguments() {
        return 2;
    }

    @Override
    public Instance applyFunctionAfterCheck(Instance inst, String resultProperties, String... arguments) {
        //Get the keyword values
        String splitKeyword = arguments[1];
        String splitChar = retrieveKeywordArgumentValue(splitKeyword, SPLIT_CHAR_KEYWORD);
        String property = arguments[0];
        String[] resultPropArr = resultProperties.split(",");
        int limit = resultPropArr.length;
        if (splitChar.equals("")) {
            logger.error("Split character for split function is not provided (empty string is NOT permitted!)");
            throw new MalformedPreprocessingFunctionException();
        }
        //Perfom the split
        for (String toSplit : inst.getProperty(property.trim())) {
            String[] splitArr = toSplit.split(Pattern.quote(splitChar), limit);
            for (int i = 0; i < splitArr.length; i++) {
                inst.addProperty(resultPropArr[i].trim(), splitArr[i]);
            }
        }
        return inst;
    }

    public boolean isComplex() {
        return true;
    }
}
