package org.aksw.limes.core.ml.algorithm;


import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.LinksetMap;
import org.aksw.limes.core.ml.algorithm.eagle.EagleParameters;
import org.aksw.limes.core.ml.algorithm.eagle.EagleUnsupervisedParameters;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public class EagleUnsupervised extends MLAlgorithm {

	EagleParameters parameters = new EagleUnsupervisedParameters();

	public EagleUnsupervised(Cache sourceCache, Cache targetCache,
			Mapping mapping) {
		super(sourceCache, targetCache, mapping);
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		return "EAGLE Unsupervised";
	}

	@Override
	public MLResult learn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinksetMap computePredictions() {
		// TODO Auto-generated method stub
		return null;
	}

}
