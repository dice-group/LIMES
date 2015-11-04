package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.Mapping;
import org.aksw.limes.core.io.cache.Cache;

public abstract class AtomicMapper implements Mapper{

    @Override
    public abstract Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression, double threshold);

    @Override
    public abstract double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language);

    @Override
    public abstract double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language);

    
}
