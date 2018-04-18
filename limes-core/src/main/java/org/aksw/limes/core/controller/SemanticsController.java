package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticsController {

    static Logger logger = LoggerFactory.getLogger(SemanticsController.class);

    public static Configuration getConfig(CommandLine cmd) {
        // 1. Determine appropriate ConfigurationReader
        String format = "xml";
        String fileNameOrUri = cmd.getArgs()[0];
        if (cmd.hasOption('f')) {
            format = cmd.getOptionValue("f").toLowerCase();
        } else if (fileNameOrUri.endsWith(".nt")
                    || fileNameOrUri.endsWith(".ttl")
                    || fileNameOrUri.endsWith(".n3")
                    || fileNameOrUri.endsWith(".rdf")) {
            format = "rdf";
        }

        AConfigurationReader reader = null;
        switch (format) {
            case "xml":
                reader = new XMLConfigurationReader(fileNameOrUri);
                break;
            case "rdf":
                reader = new RDFConfigurationReader(fileNameOrUri);
                break;
            default:
                logger.error("Error:\n\t Not a valid format: \"" + format + "\"!");
                System.exit(1);
        }

        return reader.read();
    }
}
