/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.apache.log4j.Logger;

/**
 * @author sherif
 *
 */
public class ConjunctiveWombat extends Wombat {

	static Logger logger = Logger.getLogger(ConjunctiveWombat.class.getName());
    public boolean STRICT = true;
    Set<String> measures;

    /**
     * ** TODO 
     * 1- Get relevant source and target resources from sample 
     * 2- Sample source and target caches 
     * 3- Run algorithm on samples of source and target 
     * 4- Get mapping function 
     * 5- Execute on the whole
     */
    /**
     * Constructor
     *
     * @param source
     * @param target
     * @param examples
     * @param minCoverage
     */
    public ConjunctiveWombat(Cache source, Cache target, Mapping examples, double minCoverage) {
        sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(source, minCoverage);
        targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(target, minCoverage);
        this.minCoverage = minCoverage;
        this.source = source;
        this.target = target;
        measures = new HashSet<>();
        measures.add("jaccard");
        measures.add("trigrams");
        reference = examples;
    }



    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.lgg.LGG#getMapping()
     * 
     * run conjunctive merge
     */
    public Mapping getMapping() {
        Mapping result;
        List<ExtendedClassifier> classifiers = getAllInitialClassifiers();
        result = classifiers.get(0).mapping;
        for (int i = 1; i < classifiers.size(); i++) {
            result = MappingOperations.union(result, classifiers.get(i).mapping);
        }
        return result;
    }
    
	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.lgg.LGG#getMetricExpression()
	 */
	@Override
	public String getMetricExpression() {
		String result = new String();
        List<ExtendedClassifier> classifiers = getAllInitialClassifiers();
        result = classifiers.get(0).getMetricExpression();
        for (int i = 1; i < classifiers.size(); i++) {
        	result += "OR(" + result+ "," + classifiers.get(i).getMetricExpression() + ")|0";
        }
        return result;
	}

    /**
     * Computes a sample of the reference dataset for experiments
     */
    public static Mapping sampleReference(Mapping reference, double fraction) {
        int mapSize = reference.getMap().keySet().size();
        if (fraction > 1) {
            fraction = 1 / fraction;
        }
        int size = (int) (mapSize * fraction);
        Set<Integer> index = new HashSet<>();
        //get random indexes
        for (int i = 0; i < size; i++) {
            int number;
            do {
                number = (int) (mapSize * Math.random()) - 1;
            } while (index.contains(number));
            index.add(number);
        }

        //get data
        Mapping sample = MappingFactory.createMapping(MappingType.MEMORY_MAPPING);
        int count = 0;
        for (String key : reference.getMap().keySet()) {
            if (index.contains(count)) {
                sample.getMap().put(key, reference.getMap().get(key));
            }
            count++;
        }
        return sample;
    }



}
