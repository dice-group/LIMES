package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.Mapping;
import org.aksw.limes.core.io.cache.Cache;


public interface Mapper {
    public enum Language {EN, FR, DE, NULL};
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression, double threshold);
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language);
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language);
}
