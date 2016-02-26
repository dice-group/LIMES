package org.aksw.limes.core.ml.algorithm.eagle.core;


import java.util.HashMap;
import java.util.Map.Entry;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.ProgramChromosome;
import org.aksw.limes.core.evaluation.quantity.QuantitativeMeasure;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.CacheTrimmer;
import org.apache.log4j.Logger;

/**
 * Implementation of our custom FitnessFunction.
 * As we're using the <code>DeltaFitnessEvaluator</code> higher fitness values mean the
 * individual is less fit!
 * ReImplementation on <code>ExcutionEngine</code>
 * 
 * FIXME fix QualityMeasures to work on Mappings!
 * 
 * @author Klaus Lyko
 *
 */
public class ExpressionFitnessFunction extends GPFitnessFunction implements IFitnessFunction{
	private static final long serialVersionUID = 1L;
	private static ExpressionFitnessFunction instance = null;
	static Logger logger = Logger.getLogger("LIMES");
	protected LinkSpecGeneticLearnerConfig m_config;
	/** Complete optimal Mapping. Note that it should only hold matches! */
//	protected Mapping optimalMapping;
	/** Fragment of optimal Mapping used during evolution. Note that it should only hold matches! */
	protected Mapping reference;
	/** Holding training data, that is a mapping maybe also holding non-matches.*/
	@SuppressWarnings("unused")
	private Mapping trainingData;
	protected Cache sC;
	protected Cache tC;
	protected Cache trimmedSourceCache;
	protected Cache trimmedTargetCache;
//	protected int numberOfExamples = 0;
	
	protected QuantitativeMeasure measure;
	
	protected double crossProduct;
	public static final String fScore="fScore";
	public static final String recall="recall";
	public static final String precision="precision";
	
	public ExecutionEngine engine;
	public ExecutionEngine fullEngine;

	/**
	 * Needed for subclasses.
	 */
	protected ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config) {
		m_config = a_config;
		if(a_config.sC != null)
			sC = a_config.sC;
		else {
			sC = HybridCache.getData(a_config.source);
		}
		if(a_config.tC != null)
			tC = a_config.tC;
		else
			tC = HybridCache.getData(a_config.target);
		
	}
	
	private ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config, QuantitativeMeasure measure, Mapping reference) {
		this(a_config);		
		m_config = a_config;
//		optimalMapping = reference;
		this.reference = reference;

		// get Engines
		trimKnowledgeBases(reference);
		
		fullEngine = ExecutionEngineFactory.getEngine("default", sC, tC, a_config.source.getVar(), a_config.target.getVar());
		
		this.measure=measure;
		crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
//		System.gc();
	}
	
	@Override
	protected double evaluate(IGPProgram a_subject) {
		return calculateRawFitness(a_subject);
	}
	
	/**
	 * Calculates the fitness of the given GPProgram p. This is done as follows:
	 * 1.1 get the Metric Expression by executing (as object) the first chromosome.
	 * 1.2 get the mapping links with these settings
	 * 2. Calculate either recall, precision or f-score and return 1 minus it. 
	 * The evolution could produce non wanted individuals especially those who compare properties which are 
	 * not part of the PropertyMapping (set in the ExpressionConfiguration). In such cases setp 1.1 throws an
	 * IllegalStateException. As a result the fitness value would be set to rather bad one.
	 * @param p GPProgram fitness is calculated for.
	 * @return Double value. The closer to 0 the better.
	 */
	public double calculateRawFitness(IGPProgram p) {	
		p.getGPConfiguration().clearStack();
	    p.getGPConfiguration().clearMemory();
		// get actual Mapping
		Object[] args = {};
		ProgramChromosome pc = p.getChromosome(0);
		Mapping actualMapping = new MemoryMapping();
		LinkSpecification spec = (LinkSpecification)pc.getNode(0).execute_object(pc, 0, args);
			String expr = spec.getFilterExpression();
			if(expr.indexOf("falseProp")>-1) {
				return 8d;
			}
			try{
				actualMapping = getMapping(spec, false);
			}
			catch(java.lang.OutOfMemoryError e) {
				e.printStackTrace();
				return 8d;
			}		
		
		// compare actualMap to optimalMap
		double res = getMeasure(actualMapping, reference, crossProduct);
		if(res > 1d) {
			logger.info("Error Measure > 1: "+res+". May want to normalize it?");
		}
		// get rid of Mapping
		actualMapping.getMap().clear();
		actualMapping = null;
		// this could happen
		if(Double.isNaN(res)) {//so we manually return a bad fitness value
			return 5d;			
		}		
		if(res>=0)
			return Math.abs(1.0d-res);
		else {
			return Math.abs(res)+1;
		}
	}

	public double calculateRawMeasure(IGPProgram p) {
		ProgramChromosome pc = p.getChromosome(0);
		Mapping actualMapping = new MemoryMapping();
		Object[] args = {};
		LinkSpecification spec = (LinkSpecification)pc.getNode(0).execute_object(pc, 0, args);		
			String expr = spec.getFilterExpression();
			if(expr.indexOf("falseProp")>-1) {
				return 0d;
			}
			try{
				actualMapping = getMapping(spec, false);
			}
			catch(java.lang.OutOfMemoryError e) {
				e.printStackTrace();
				return 0d;
			}		
		
		// compare actualMap to optimalMap
		return getMeasure(actualMapping, reference, crossProduct);
	}
	
	
	/**
	 * Return either recall, precision of (default) f-score of the given mappings.
	 * @param a_mapping Mapping to be analyzed. 
	 * @param reference Reference mapping.
	 * @return
	 */
	private double getMeasure(Mapping a_mapping, Mapping reference, double crossProduct) {
		double quality = measure.calculate(a_mapping, reference);
		// TODO check
		return quality;
	}
	
	
	public static ExpressionFitnessFunction getInstance(LinkSpecGeneticLearnerConfig a_config, QuantitativeMeasure measure,  Mapping reference) {
		if(instance == null) {
				instance = new ExpressionFitnessFunction(a_config, measure, reference);
		}
		return instance;
	}

	/**
	 * Get full caches used by this learner.
	 * @return HybridCache of the source endpoint.
	 */
	public Cache getSourceCache() {
		return sC;
	}
	/**
	 * Get full caches used by this learner.
	 * @return HybridCache of the target endpoint.
	 */
	public Cache getTargetCache() {
		return tC;
	}
	
	public void destroy() {
		instance = null;
	}

	/**
	 * Method to create the mapping based on the specified expression and acceptance threshold.
	 * As of now this just wraps around the the SetConstraintsMapper getLinks() function.
	 * @param expression LIMES metric expression used to compare instances of the source and target knowledgebases.
	 * @param accThreshold global acceptance threshold.
	 * @param full if set use full otherwise trimmed caches.
	 * @return
	 */
	public Mapping getMapping(LinkSpecification spec, boolean full) {
		try {
			if(full) {
				IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerFactory.DEFAULT,
						sC, tC);
				NestedPlan plan = planner.plan(spec);		
				return fullEngine.execute(plan);
			} else {
				IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerFactory.DEFAULT,
						trimmedSourceCache, trimmedTargetCache);
				NestedPlan plan = planner.plan(spec);		
				return engine.execute(plan);
			}
		}catch(Exception e) {
			logger.error("Exception execution expression "+spec+ " : full? " + full);
			return new MemoryMapping();
		}
		
		catch(java.lang.OutOfMemoryError e) {
			logger.warn("Out of memory trying to get Map for expression\""+spec+"\".");
			return new MemoryMapping();
		}
	}
	
	/**
	 * Method to scale down caches according to given training data.
	 * @param trainingData Mapping holding data instances a user has evaluated. That may include non-matches.
	 */
	public void trimKnowledgeBases(Mapping trainingData) {
			this.trainingData = trainingData;
			Cache[] trimmed = CacheTrimmer.processData(sC, tC, trainingData);
			trimmedSourceCache = trimmed[0];
			trimmedTargetCache = trimmed[1];		
			logger.info("Trimming to "+trimmed[0].size()+" and "+trimmed[1].size()+" caches.");
			crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
			engine = ExecutionEngineFactory.getEngine("default", 
					trimmedSourceCache, trimmedTargetCache, 
					this.m_config.source.getVar(), this.m_config.target.getVar());

	}
	
	/**
	 * As we assume referenceData only holding matches. Learner may have to set it separately.
	 * @param referenceData A Mapping holding all matches.
	 */
	public void setReferenceMapping(Mapping referenceData) {
		reference = referenceData;
	}
	
	/**
	 * If we want to use Full Caches instead.
	 * @param value
	 */
	public void useFullCaches(boolean value) {
		if(value) {
			engine = ExecutionEngineFactory.getEngine("default", 
					trimmedSourceCache, trimmedTargetCache, 
					this.m_config.source.getVar(), this.m_config.target.getVar());
		}
		else {
			engine = ExecutionEngineFactory.getEngine("default", 
					sC, tC,
					this.m_config.source.getVar(), this.m_config.target.getVar());
		}
	}

	public LinkSpecification getMetric(IGPProgram p) {
		Object[] args = {};
		ProgramChromosome pc = p.getChromosome(0);
		return (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
	}
	
	public Mapping getReferenceMapping() {
		return reference;
	}
	
	/**
	 * Method to add instances to reference?.
	 * @param m Mapping of matches, designated as such by an oracle.
	 */
	public void addToReference(Mapping m) {
		logger.info("Filling reference of size "+reference.size()+" with "+m.size()+" additional matches.");
		for(Entry<String, HashMap<String, Double>> e1 : m.getMap().entrySet()) {
			for(Entry<String, Double> e2 : e1.getValue().entrySet()) {
				reference.add(e1.getKey(), e2.getKey(), 1d);
			}
		}
		logger.info("Reference has now "+reference.size()+" Matches.");
	}
	
	public void fillCachesIncrementally(Mapping matches) {
		for(String sUri:matches.getMap().keySet())
			for(String tUri:matches.getMap().get(sUri).keySet()) {
				if(!trimmedSourceCache.containsUri(sUri)) {
					logger.info("Adding instance "+sUri+" to sC");
					trimmedSourceCache.addInstance(sC.getInstance(sUri));
				}
				if(!trimmedTargetCache.containsUri(tUri)) {
					logger.info("Adding instance "+tUri+" to tC");
					trimmedTargetCache.addInstance(tC.getInstance(tUri));
				}
			}
		engine = ExecutionEngineFactory.getEngine("default", 
				trimmedSourceCache, trimmedTargetCache,
				this.m_config.source.getVar(), this.m_config.target.getVar());
		crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
	}
	
	public void setCaches(Cache sC, Cache tC) {
		this.sC = sC;
		this.tC = tC;
	}
}
