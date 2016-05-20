/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.apache.log4j.Logger;

/**
 * @author sherif
 *
 */
public class ConjunctiveWombat extends Wombat {
	protected static final String ALGORITHM_NAME = "Conjunctive Wombat";

	static Logger logger = Logger.getLogger(ConjunctiveWombat.class.getName());
	public boolean STRICT = true;
	Set<String> measures;

	/**
	 * Constructor
	 *
	 * @param sourceCache
	 * @param targetCache
	 * @param examples
	 * @param minCoverage
	 */
	public ConjunctiveWombat(Cache sourceCache, Cache targetCache, Mapping examples, Configuration configuration) {
		super(sourceCache, targetCache, configuration);
		measures = new HashSet<>();
		measures.add("jaccard");
		measures.add("trigrams");
		reference = examples;
	}




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
		Mapping sample = MappingFactory.createMapping(MappingType.DEFAULT);
		int count = 0;
		for (String key : reference.getMap().keySet()) {
			if (index.contains(count)) {
				sample.getMap().put(key, reference.getMap().get(key));
			}
			count++;
		}
		return sample;
	}



	@Override
	public String getName() {
		return ALGORITHM_NAME;
	}



	@Override
	public MLModel learn(Mapping trainingData) {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * run conjunctive merge
	 */
	@Override
	public Mapping computePredictions() {
		Mapping result;
		List<ExtendedClassifier> classifiers = getAllInitialClassifiers();
		result = classifiers.get(0).mapping;
		for (int i = 1; i < classifiers.size(); i++) {
			result = MappingOperations.union(result, classifiers.get(i).mapping);
		}
		return result;
	}



	@Override
	public void init(LearningSetting parameters, Mapping trainingData) throws Exception {
		// TODO Auto-generated method stub

	}



	@Override
	public void terminate() {
		// TODO Auto-generated method stub

	}




	@Override
	public Set<String> parameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
