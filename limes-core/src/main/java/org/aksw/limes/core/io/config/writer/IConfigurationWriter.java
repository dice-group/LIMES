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
package org.aksw.limes.core.io.config.writer;

import org.aksw.limes.core.io.config.Configuration;

import java.io.IOException;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 8, 2016
 */
public interface IConfigurationWriter {

    /**
     * Write the configuration object to outputFile in the given format
     *
     * @param configuration object to be Written
     * @param outputFile to write the configuration object to
     * @param format of the outputFile
     * @throws IOException if the output file can not be created
     */
    void write(Configuration configuration, String outputFile, String format) throws IOException;

    /**
     * Write the configuration object to outputFile detecting the format from outputFile extension
     *
     * @param configuration object to be Written
     * @param outputFile to write the configuration object to
     * @throws IOException if the output file can not be created
     */
    void write(Configuration configuration, String outputFile) throws IOException;

}
