package org.aksw.limes.core.io.mapping.writer;

import java.io.IOException;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public interface IMappingWriter {

    void write(AMapping mapping, String outputFile) throws IOException;

    void write(AMapping mapping, String outputFile, String format) throws IOException;
}
