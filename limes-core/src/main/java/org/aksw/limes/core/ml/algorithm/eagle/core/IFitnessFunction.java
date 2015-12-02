package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.io.mapping.Mapping;
import org.jgap.gp.IGPProgram;

public interface IFitnessFunction {

	public Mapping getMapping(String expression, double accThreshold, boolean full);
	
	public double calculateRawFitness(IGPProgram p);

	public double calculateRawMeasure(IGPProgram bestHere);
}
