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
package org.aksw.limes.core.evaluation.oracle;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.reader.AMappingReader;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import de.uni_leipzig.simba.learning.oracle.mappingreader.XMLMappingReader;


/**
 * Factory class that gives different types of oracles based on the file type
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class OracleFactory {
    static Logger logger = LoggerFactory.getLogger(OracleFactory.class);

    /**
     * Creates an oracle based on the input type (i.e., the type of file within which the
     * oracle data is contained) and the type of oracle needed.
     *
     * @param filePath
     *         Path to the file containing the data encapsulated by the oracle
     * @param inputType
     *         Type of the file
     * @param oracleType
     *         Type of oracle required
     * @return An oracle that contains the data found at filePath
     */
    public static IOracle getOracle(String filePath, String inputType, String oracleType) {
        AMappingReader reader = null;
        IOracle oracle;
        System.out.println("Getting reader of type " + inputType);
        if (inputType.equalsIgnoreCase("csv")) //scan input types here
        {
            reader = new CSVMappingReader(filePath);
        } else if (inputType.equalsIgnoreCase("rdf")) //scan input types here
        {
            reader = new RDFMappingReader(filePath);
        } else if (inputType.equalsIgnoreCase("tab")) //scan input types here
        {
            reader = new CSVMappingReader(filePath);
            ((CSVMappingReader) reader).setDelimiter("\t");
        } else //default
        {
            reader = new CSVMappingReader(filePath);
        }
        //now readData
        AMapping m = reader.read();

        //finally return the right type of oracle
        if (oracleType.equals("simple")) //scan input types here
        {
            oracle = new SimpleOracle(m);
        } else //default
        {
            oracle = new SimpleOracle(m);
        }
        //        oracle.loadData(m);
        return oracle;
    }
}

