package org.aksw.limes.core.io.mapping.writer;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public interface IMappingWriter {
	boolean write(Mapping mapping, String file);
}
