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
package org.aksw.limes.core.measures.mapper.pointsets;

import org.aksw.limes.core.io.parser.Parser;

import java.util.Arrays;
import java.util.List;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class PropertyFetcher {

    public static List<String> getProperties(String expression, double threshold) {
        // get property labels
        Parser p = new Parser(expression, threshold);
        return Arrays.asList(getPropertyLabel(p.getLeftTerm()), getPropertyLabel(p.getRightTerm()));
    }

    private static String getPropertyLabel(String term) {
        String propertyLabel;
        if (term.contains(".")) {
            String split[] = term.split("\\.");
            propertyLabel = split[1];
            if (split.length >= 2) {
                for (int part = 2; part < split.length; part++) {
                    propertyLabel += "." + split[part];
                }
            }
        } else {
            propertyLabel = term;
        }
        return propertyLabel;
    }
}
