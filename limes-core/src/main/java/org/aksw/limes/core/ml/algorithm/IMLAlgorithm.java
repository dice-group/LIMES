package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.ml.LinksetMap;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public interface IMLAlgorithm {

	public String getName();

	public MLResult learn();
	
	public LinksetMap computePredictions();
	
}
