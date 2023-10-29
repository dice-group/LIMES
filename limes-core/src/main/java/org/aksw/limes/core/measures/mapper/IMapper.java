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
package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the mapper interface.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IMapper {
    /**
     * Returns a mapping given a source, a target knowledge base and a link
     * specification.
     *
     * @param source
     *            source cache
     * @param target
     *            target cache
     * @param sourceVar
     *            source property variable
     * @param targetVar
     *            size property variable
     * @param expression
     *            metric expression of link specification
     * @param threshold
     *            threshold of link specification
     * @return a mapping, the resulting mapping
     */
    AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                        double threshold);

    ;

    /**
     * Returns the estimated time needed to obtain the mapping computed by the
     * mapper.
     *
     * @param sourceSize
     *            source size
     * @param targetSize
     *            target size
     * @param theta
     *            atomic specification threshold
     * @param language
     *            language of source and target variables
     * @return estimated runtime, as double
     */
    double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language);

    /**
     * Returns the estimated mapping size of the mapping computed by the mapper.
     *
     * @param sourceSize
     *            source size
     * @param targetSize
     *            target size
     * @param theta
     *            atomic specification threshold
     * @param language
     *            language of source and target variables
     * @return estimated execution time, as double
     */
    double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language);

    /**
     * Returns the name of the mapper.
     *
     * @return Mapper name as a string
     */
    String getName();

    enum Language {
        EN, FR, DE, NULL
    }

    void setNo(int no);
}
