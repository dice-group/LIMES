package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.Mapping;

public interface IMappingReader {
	
	public abstract Mapping read(String file);
}
