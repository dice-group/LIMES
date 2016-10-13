package org.aksw.limes.core.io.config.writer;

import java.io.IOException;

import org.aksw.limes.core.io.config.Configuration;

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
