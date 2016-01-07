package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public interface IMappingReader {
	
	public abstract Mapping read(String file);
}
