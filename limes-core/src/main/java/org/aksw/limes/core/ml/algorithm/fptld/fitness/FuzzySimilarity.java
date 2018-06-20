package org.aksw.limes.core.ml.algorithm.fptld.fitness;

import org.aksw.limes.core.io.mapping.AMapping;

public interface FuzzySimilarity {
	double getSimilarity(AMapping a, AMapping b);

}
