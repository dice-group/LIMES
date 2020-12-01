package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.jgap.gp.IGPProgram;
/**
 * Basic interface for EAGLEs fitness functions
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public interface IFitnessFunction {
	/**
	 * To calculate Mappings based on LS
	 * @param sourceCache
	 * @param targetCache
	 * @param spec
	 * @return
	 */
    public AMapping getMapping(ACache sourceCache, ACache targetCache, LinkSpecification spec);

    public double calculateRawFitness(IGPProgram p);

    public double calculateRawMeasure(IGPProgram bestHere);
}
