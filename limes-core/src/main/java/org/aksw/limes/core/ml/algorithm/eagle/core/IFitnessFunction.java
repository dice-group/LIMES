package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.jgap.gp.IGPProgram;

public interface IFitnessFunction {

    public AMapping getMapping(LinkSpecification spec, boolean full);

    public double calculateRawFitness(IGPProgram p);

    public double calculateRawMeasure(IGPProgram bestHere);
}
