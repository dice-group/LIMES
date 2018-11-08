package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the mapper interface.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IMapper {
    /**
     * Returns a mapping given a source, a target knowledge base and a link
     * specification.
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
    AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold);

    ;

    /**
     * Returns the estimated time needed to obtain the mapping computed by the
     * mapper.
     *
     * @param sourceSize
     *            source size
     * @param targetSize
     *            target size
     * @param theta
     *            atomic specification threshold
     * @param language
     *            language of source and target variables
     * @return estimated runtime, as double
     */
    double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language);

    /**
     * Returns the estimated mapping size of the mapping computed by the mapper.
     *
     * @param sourceSize
     *            source size
     * @param targetSize
     *            target size
     * @param theta
     *            atomic specification threshold
     * @param language
     *            language of source and target variables
     * @return estimated execution time, as double
     */
    double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language);

    /**
     * Returns the name of the mapper.
     *
     * @return Mapper name as a string
     */
    String getName();

    enum Language {
        EN, FR, DE, NULL
    }

    void setNo(int no);
}
