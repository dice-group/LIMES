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
