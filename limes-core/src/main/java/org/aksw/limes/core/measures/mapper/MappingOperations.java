package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.LukasiewiczSetOperations;

// Classes should be enum to ensure they are singletons
public interface MappingOperations {

	AMapping difference(AMapping map1, AMapping map2);

	AMapping difference(AMapping map1, AMapping map2, double[] parameters);

	AMapping intersection(AMapping map1, AMapping map2);

	AMapping intersection(AMapping map1, AMapping map2, double[] parameters);

	AMapping union(AMapping map1, AMapping map2);

	AMapping union(AMapping map1, AMapping map2, double[] parameters);

	static MappingOperations getInstanceByEnum(Command c) {
		if (Command.lukasiewicz.contains(c)) {
			return LukasiewiczSetOperations.INSTANCE;
		} else {
			return CrispSetOperations.INSTANCE;
		}
	}
}
