package org.aksw.limes.core.io.config.writer;

import org.aksw.limes.core.io.config.Configuration;

import java.io.IOException;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public interface IConfigurationWriter {

    /**
     * Write the configuration object to outputFile in the given format
     *
     * @param configuration
     * @param outputFile
     * @param format
     *         of the outputFile
     * @throws IOException
     */
    void write(Configuration configuration, String outputFile, String format) throws IOException;

    /**
     * Write the configuration object to outputFile detecting the format from outputFile extension
     *
     * @param configuration
     * @param outputFile
     * @throws IOException
     */
    void write(Configuration configuration, String outputFile) throws IOException;

}
