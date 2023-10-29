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
package org.aksw.limes.core.io.mapping;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Mohamed Sherif {@literal <}sherif {@literal @} informatik.uni-leipzig.de{@literal >}
 * @version Nov 12, 2015
 */
public class MappingFactory {
    private static final Logger logger = LoggerFactory.getLogger(MappingFactory.class.getName());

    /**
     * @return the default Mapping implementation
     */
    public static AMapping createDefaultMapping() {
        return new MemoryMapping();
    }

    /**
     * @param type of mapping
     * @return a specific module instance given its module's name
     * @author sherif
     */
    public static AMapping createMapping(MappingType type) {
        if (type == MappingType.DEFAULT)
            return createDefaultMapping();
        if (type == MappingType.MEMORY_MAPPING)
            return new MemoryMapping();
        if (type == MappingType.HYBIRD_MAPPING)
            return new HybridMapping();
        if (type == MappingType.FILE_MAPPING)
            return new FileMapping();
        logger.warn("Sorry, " + type + " is not yet implemented. Generating " + MappingType.DEFAULT + " map ...");
        return createDefaultMapping();
    }

    public enum MappingType {
        DEFAULT, // currently memory mapping
        MEMORY_MAPPING,
        HYBIRD_MAPPING,
        FILE_MAPPING
    }


}
