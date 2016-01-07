package org.aksw.limes.core.io.config.reader;

import org.aksw.limes.core.io.config.Configuration;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public interface IConfigurationReader {
	
	Configuration configuration = new Configuration();

	/**
	 * @param filePath
	 * @return filled configuration object from the input file
	 */
	Configuration read(String filePath);
	
}
