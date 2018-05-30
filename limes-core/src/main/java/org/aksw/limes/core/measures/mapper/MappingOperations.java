package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.mapping.AMapping;

// Classes should be enum to ensure they are singletons
public interface MappingOperations {

	AMapping difference(AMapping map1, AMapping map2);

	AMapping intersection(AMapping map1, AMapping map2);

	AMapping union(AMapping map1, AMapping map2);
}
