package org.aksw.limes.core.ml.algorithm;

import java.util.Set;

import org.aksw.limes.core.ml.LearningModel;
import org.aksw.limes.core.ml.Prediction;
import org.aksw.limes.core.model.Link;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public interface MLAlgorithm {

	public String getName();

	public void learn();

	public Prediction predict(Link link);

	public Set<Link> computePredictions();
	
	public LearningModel getLearningModel();

}
