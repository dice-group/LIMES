package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;

public interface IMapper {
    public enum Language {
	EN, FR, DE, NULL
    };

    /**
     * Returns a mapping given a source, a target knowledge base and a link
     * specification
     *
     * @param source
     *            source cache
     * @param target
     *            target cache
     * @param sourceVar
     *            source property variable
     * @param targetVar
     *            size property variable
     * @param expression
     *            metric expression of link specification
     * @param threshold
     *            threshold of link specification
     * @return a mapping, the resulting mapping
     */
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold);
    /**
     * Returns the estimated time needed to obtain the mapping computed by a mapper
     * specification
     *
     * @param sourceSize
     *            source size
     * @param targetSize
     *            target size
     * @param theta
     *            atomic specification threshold
     * @param language 
     *            language of source and target variables
     * 
     * @return estimated runtime, as double
     */
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language);
    /**
     * Returns the estimated mapping size of the mapping computed by a mapper
     * specification
     *
     * @param sourceSize
     *            source size
     * @param targetSize
     *            target size
     * @param theta
     *            atomic specification threshold
     * @param language 
     *            language of source and target variables
     * 
     * @return estimated execution time, as double
     */
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language);
    /**
     * Returns the name of the current Mapper
     *
     * 
     * @return Mapper name as a string
     */
    public String getName();
}
