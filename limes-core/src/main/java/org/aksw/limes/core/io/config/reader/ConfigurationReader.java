package org.aksw.limes.core.io.config.reader;

import org.aksw.limes.core.io.config.Configuration;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public abstract class ConfigurationReader {
	
	public Configuration configuration = new Configuration();
	/**
	 * @param filePath
	 * @return filled configuration object from the input file
	 */
	abstract public Configuration read(String filePath);
	
}
