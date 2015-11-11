package org.aksw.limes.core.io.config.writer;

import java.io.IOException;

import org.aksw.limes.core.io.config.Configuration;

public interface IConfigurationWriter {

	void write(Configuration configuration, String outputFile, String format) throws IOException;
	void write(Configuration configuration, String outputFile) throws IOException;

}
