package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.io.mapping.AMapping;
import org.jgap.gp.GPFitnessFunction;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public abstract class IGPFitnessFunction extends GPFitnessFunction implements IFitnessFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -94163100342394354L;
	
	public abstract void addToReference(AMapping m);
	
	public abstract void fillCachesIncrementally(AMapping matches);
	
}
