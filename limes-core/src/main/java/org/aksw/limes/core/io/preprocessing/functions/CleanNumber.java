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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Removes type information from number properties
 * @author Daniel Obraczka
 *
 */
public class CleanNumber extends APreprocessingFunction implements IPreprocessingFunction {
    static Logger logger = LoggerFactory.getLogger(CleanNumber.class);
    /**
     * Matches a number that is followed by "^"
     */
    public static final Pattern typedNumber = Pattern.compile("[0-9,.\\-E]+(?=\\^)");
    public static final Pattern untypedNumber = Pattern.compile("-?[0-9E]+(\\.[0-9E]+)?");

    @Override
    public Instance applyFunctionAfterCheck(Instance i, String property, String... arguments) {
        TreeSet<String> oldValues = i.getProperty(property);
        TreeSet<String> newValues = new TreeSet<>();
        for (String value : oldValues) {
            newValues.add(removeTypeInformation(value));
        }
        i.replaceProperty(property, newValues);
        return i;
    }

    /**
     * Removes type information from number propertyerties, e.g.
     * "10^^http://www.w3.org/2001/XMLSchema#positiveInteger" would become "10"
     *
     * @param number
     * @return number without type information as String or 0
     */
    public static String removeTypeInformation(String number) {
        if (untypedNumber.matcher(number).matches()){
            return number;
        }

        Matcher m = typedNumber.matcher(number);
        String newValue;
        if (m.find()) {
            newValue = m.group();
            try {
                // Check if it is a parseable double
                newValue = new DecimalFormat("###.#############################", new DecimalFormatSymbols(Locale.US)).format(Double.parseDouble(newValue));
            } catch (Exception e) {
                logger.error(newValue + " is not a parseable double\n Using 0 instead");
                return 0 + "";
            }
        } else {
            logger.error(number + " is not a typed double\n Using 0 instead");
            return 0 + "";
        }
        return newValue;
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
