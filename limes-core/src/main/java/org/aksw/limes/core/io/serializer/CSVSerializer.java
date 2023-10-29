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
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 25, 2015
 */
public class CSVSerializer extends TabSeparatedSerializer {
    private static Logger logger = LoggerFactory.getLogger(CSVSerializer.class.getName());

    protected String separator = ",";

    public String getName() {
        return "CommaSeparatedSerializer";
    }

    @Override
    public void printStatement(String subject, String predicate, String object, double similarity) {
        try {
            writer.println("\"" + subject + "\"" + separator + "\"" + object + "\"" + separator + similarity);
        } catch (Exception e) {
            logger.warn("Error writing");
        }
    }

    public String getFileExtension() {
        return "csv";
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }


}
