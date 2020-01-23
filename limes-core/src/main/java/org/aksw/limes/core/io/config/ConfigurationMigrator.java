package org.aksw.limes.core.io.config;

import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.config.writer.RDFConfigurationWriter;

import java.io.IOException;

/**
 *
 */
public class ConfigurationMigrator {

    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        String outputFile = args[1];
        new RDFConfigurationWriter().write(new XMLConfigurationReader(inputFile).read(), outputFile);
    }

}
