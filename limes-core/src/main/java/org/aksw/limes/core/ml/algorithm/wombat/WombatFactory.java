/**
 * 
 */
package org.aksw.limes.core.ml.algorithm.wombat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.Logger;



/**
 * @author sherif
 *
 */
public class WombatFactory {
	private static final Logger logger = Logger.getLogger(WombatFactory.class.getName());

	protected static final String CONJUNCTIVE 			= "conjunctive";
	protected static final String SIMPLE				= "simple";
	protected static final String COMPLETE 				= "complete";
	protected static final String WEAK 					= "weak";
	protected static final String UNSUPERVISED_SIMPLE 	= "unsupervised simple";
	protected static final String UNSUPERVISED_COMPLETE = "unsupervised complete";

	public enum WombatType{
		SIMPLE, COMPLETE, CONJUNCTIVE, WEAK, UNSUPERVISED_SIMPLE, UNSUPERVISED_COMPLETE
	};

	/**
	 * @param name
	 * @return a specific module instance given its module's name
	 * @author sherif
	 */
	public static Wombat createOperator(WombatType type, Cache source, Cache target, Mapping examples, double minCoverage, Configuration config) {
		if(type == WombatType.SIMPLE)
			return new SimplWombat(source, target, examples, minCoverage, config);
		if(type == WombatType.COMPLETE )
			return new CompleteWombat(source, target, examples, minCoverage, config);
		if(type == WombatType.CONJUNCTIVE)
			return new ConjunctiveWombat(source, target, examples, minCoverage, config);
		if(type == WombatType.WEAK)
			return new WeakWombat(source, target, examples, minCoverage, config);
		if(type == WombatType.UNSUPERVISED_SIMPLE)
			return new UnsupervisedSimpleWombat(source, target, examples, minCoverage, config);
		if(type == WombatType.UNSUPERVISED_COMPLETE)
			return new UnsupervisedCompleteWombat(source, target, examples, minCoverage, config);
		return null;
	}


	/**
	 * @return list of names of all implemented operators
	 * @author sherif
	 */
	public static List<String> getNames(){
		return new ArrayList<String>(Arrays.asList(CONJUNCTIVE, SIMPLE, COMPLETE , WEAK));
	}

}