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
package org.aksw.limes.core.measures.measure.space;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class SpaceMeasureFactory {

    static Logger logger = LoggerFactory.getLogger(SpaceMeasureFactory.class);

    public static ISpaceMeasure getMeasure(String name, int dimension) {
        // System.out.println("SpaceMesure.getMeasure("+name+")");
        if (name.toLowerCase().startsWith("geo")) {
            if (dimension != 2) {
                logger.warn("Erroneous dimension settings for GeoDistance (" + dimension + ").");
            }
            return new GeoOrthodromicMeasure();
        } else {
            EuclideanMeasure measure = new EuclideanMeasure();
            measure.setDimension(dimension);
            return measure;
        }
    }
}
