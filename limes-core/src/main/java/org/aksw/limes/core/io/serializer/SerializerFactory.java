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
package org.aksw.limes.core.io.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 25, 2015
 */
public class SerializerFactory {
    private static Logger logger = LoggerFactory.getLogger(SerializerFactory.class.getName());

    public static ISerializer createSerializer(String name) {
        logger.info("Getting serializer with name " + name);
        if (name == null)
            return new NtSerializer();
        if (name.toLowerCase().trim().startsWith("tab"))
            return new TabSeparatedSerializer();
        if (name.toLowerCase().trim().startsWith("csv"))
            return new CSVSerializer();
        if (name.toLowerCase().trim().startsWith("ttl") || name.toLowerCase().trim().startsWith("turtle"))
            return new TTLSerializer();
        if (name.toLowerCase().trim().startsWith("nt") || name.toLowerCase().trim().startsWith("n3"))
            return new NtSerializer();
        else {
            logger.info("Serializer with name " + name + " not found. Using .nt as default format.");
            return new NtSerializer();
        }
    }


    /**
     * Get all available serializer.
     *
     * @return Array of Serializers.
     */
    public static ISerializer[] getAllSerializers() {
        return new ISerializer[]{createSerializer("nt"), createSerializer("csv"), createSerializer("tab"), createSerializer("ttl")};
    }
}
