package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.cache.Cache;
import org.aksw.limes.core.result.result.Mapping;


public interface Mapper {
    public enum Language {EN, FR, DE, NULL};
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression, double threshold);
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language);
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language);
}
