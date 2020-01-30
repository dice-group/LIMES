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

