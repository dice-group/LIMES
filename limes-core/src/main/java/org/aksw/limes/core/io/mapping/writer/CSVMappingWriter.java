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
package org.aksw.limes.core.io.mapping.writer;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.serializer.CSVSerializer;

import java.io.IOException;


/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public class CSVMappingWriter implements IMappingWriter {


    /*
     * Serialize the input mapping a file in the CSV format with the default comma separator
     *
     * (non-Javadoc)
     * @see org.aksw.limes.core.io.mapping.writer.IMappingWriter#write(org.aksw.limes.core.io.mapping.AMapping, java.lang.String)
     */
    @Override
    public void write(AMapping mapping, String outputFile) throws IOException {
        write(mapping, outputFile, ",");
    }


    /*
     * Serialize the input mapping a file in the CSV format with the input separator
     *
     * (non-Javadoc)
     * @see org.aksw.limes.core.io.mapping.writer.IMappingWriter#write(org.aksw.limes.core.io.mapping.AMapping, java.lang.String, java.lang.String)
     */
    @Override
    public void write(AMapping mapping, String outputFile, String separator)
            throws IOException {
        CSVSerializer csvSerializer = new CSVSerializer();
        csvSerializer.setSeparator(separator);
        csvSerializer.writeToFile(mapping, "", outputFile);
    }


}
