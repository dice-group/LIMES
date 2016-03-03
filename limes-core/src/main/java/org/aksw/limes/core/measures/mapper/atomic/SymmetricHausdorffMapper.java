/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.MappingOperations;

/**
 *
 * @author ngonga
 */
public class SymmetricHausdorffMapper extends Mapper {
    /**
     * Computes a mapping between a source and a target.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Variable for the source dataset
     * @param targetVar
     *            Variable for the target dataset
     * @param expression
     *            Expression to process.
     * @param threshold
     *            Similarity threshold
     * @return A mapping which contains links between the source instances and
     *         the target instances
     */
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	OrchidMapper hm = new OrchidMapper();
	Mapping m1 = hm.getMapping(source, target, sourceVar, targetVar, expression, threshold);
	Mapping m2 = hm.getMapping(target, source, targetVar, sourceVar, expression, threshold);
	m2 = m2.reverseSourceTarget();
	m1 = MappingOperations.intersection(m1, m2);
	return m1;
    }

    public String getName() {
	return "Symmetric Hausdorff";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return (new OrchidMapper()).getRuntimeApproximation(sourceSize, targetSize, theta, language);
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return (new OrchidMapper()).getMappingSizeApproximation(sourceSize, targetSize, theta, language);
    }

}
