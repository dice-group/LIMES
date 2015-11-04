package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.Mapping;
import org.aksw.limes.core.io.cache.Cache;

public abstract class AtomicMapper implements Mapper{

    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression, double threshold) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	// TODO Auto-generated method stub
	return 0;
    }

    
}
