package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Method stub of new implementation.
 * TODO adjust to new execution engine
 * TODO adjust to new interfaces 
 * @author lyko
 *s
 */
public class ExpressionFitnessFunction extends GPFitnessFunction implements IFitnessFunction{

	@Override
	public Mapping getMapping(String expression, double accThreshold, boolean full) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateRawFitness(IGPProgram p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double calculateRawMeasure(IGPProgram bestHere) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double evaluate(IGPProgram arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

//	private static final long serialVersionUID = -6546938780090355359L;
//	private static ExpressionFitnessFunction instance = null;
//	static Logger logger = Logger.getLogger("LIMES");
//	protected LinkSpecGeneticLearnerConfig m_config;
//	protected SetConstraintsMapper sCM, sCMFull;
//	/** Complete optimal Mapping. Note that it should only hold matches! */
//	protected Mapping optimalMapping;
//	/** Fragment of optimal Mapping used during evolution. Note that it should only hold matches! */
//	protected Mapping reference;
//	/** Holding training data, that is a mapping maybe also holding non-matches.*/
//	@SuppressWarnings("unused")
//	private Mapping trainingData;
//	protected Cache sC;
//	protected Cache tC;
//	protected Cache trimmedSourceCache;
//	protected Cache trimmedTargetCache;
//	protected int numberOfExamples = 0;
//	Filter f;
//	
//	private int granualarity = 2;
//	protected String measure;
//	
//	protected double crossProduct;
//	public static final String fScore="fScore";
//	public static final String recall="recall";
//	public static final String precision="precision";
//	public static final String mcc="MCC";
//
//	/**
//	 * Needed for subclasses.
//	 */
//	protected ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config) {
//		//super();
//		f = new LinearFilter();
//		m_config = a_config;
//		if(a_config.sC != null)
//			sC = a_config.sC;
//		else {
//			sC = HybridCache.getData(a_config.source);
//		}
//		if(a_config.tC != null)
//			tC = a_config.tC;
//		else
//			tC = HybridCache.getData(a_config.target);
//		
//	}
//	private ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config, Oracle o, String measure, int sampleSize) {
//		this(a_config, o.getMapping(), measure, sampleSize);
//	}
//	
//	private ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config, Mapping reference, String measure, int sampleSize) {
//		this(a_config);		
//		m_config = a_config;
//		this.numberOfExamples = sampleSize;
//		optimalMapping = reference;
//		this.reference = reference;
//
//		// get Mapper
//		trimKnowledgeBases(reference);
//		sCM = SetConstraintsMapperFactory.getMapper( "simple", a_config.source, a_config.target, 
//				trimmedSourceCache, trimmedTargetCache, f, 2);
//		sCMFull = SetConstraintsMapperFactory.getMapper( "simple",  a_config.source, a_config.target, 
//				sC, tC, f, 2);
//		this.measure=measure;
//		crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
//		System.gc();
//	}
//	
//	@Override
//	protected double evaluate(IGPProgram a_subject) {
//		return calculateRawFitness(a_subject);
//	}
//	
//	/**
//	 * Calculates the fitness of the given GPProgram p. This is done as follows:
//	 * 1.1 get the Metric Expression by executing (as object) the first chromosome.
//	 * 1.2 get the mapping links with these settings
//	 * 2. Calculate either recall, precision or f-score and return 1 minus it. 
//	 * The evolution could produce non wanted individuals especially those who compare properties which are 
//	 * not part of the PropertyMapping (set in the ExpressionConfiguration). In such cases setp 1.1 throws an
//	 * IllegalStateException. As a result the fitness value would be set to rather bad one.
//	 * @param p GPProgram fitness is calculated for.
//	 * @return Double value. The closer to 0 the better.
//	 */
//	public double calculateRawFitness(IGPProgram p) {	
//		p.getGPConfiguration().clearStack();
//	    p.getGPConfiguration().clearMemory();
//		// get actual Mapping
//		Object[] args = {};
//		ProgramChromosome pc = p.getChromosome(0);
//		Mapping actualMapping = new MemoryMapping();
//		Metric metric = (Metric)pc.getNode(0).execute_object(pc, 0, args);
//			String expr = metric.getExpression();
//			double accThreshold = metric.getThreshold();
//			if(expr.indexOf("falseProp")>-1) {
//				return 8d;
//			}
//			try{
//				actualMapping = getMapping(expr, accThreshold, false);
//			}
//			catch(java.lang.OutOfMemoryError e) {
//				e.printStackTrace();
//				return 8d;
//			}		
//		
//		// compare actualMap to optimalMap
//		double res = getMeasure(actualMapping, reference, crossProduct);
//		if(res > 1d) {
//			logger.info("Error Measure > 1: "+res+". May want to normalize it?");
//		}
//		// get rid of Mapping
//		actualMapping.getMap().clear();
//		actualMapping = null;
//		// this could happen
//		if(Double.isNaN(res)) {//so we manually return a bad fitness value
//			return 5d;			
//		}		
//		if(res>=0)
//			return Math.abs(1.0d-res);
//		else {
//			return Math.abs(res)+1;
//		}
//	}
//
//	public double calculateRawMeasure(IGPProgram p) {
//		ProgramChromosome pc = p.getChromosome(0);
//		Mapping actualMapping = new Mapping();
//		Object[] args = {};
//		Metric metric = (Metric)pc.getNode(0).execute_object(pc, 0, args);		
//			String expr = metric.getExpression();
//			double accThreshold = metric.getThreshold();
//			if(expr.indexOf("falseProp")>-1) {
//				return 0d;
//			}
//			try{
//				actualMapping = getMapping(expr, accThreshold, false);
//			}
//			catch(java.lang.OutOfMemoryError e) {
//				e.printStackTrace();
//				return 0d;
//			}		
//		
//		// compare actualMap to optimalMap
//		return getMeasure(actualMapping, reference, crossProduct);
//	}
//	
//	
//	/**
//	 * Return either recall, precision of (default) f-score of the given mappings.
//	 * @param a_mapping Mapping to be analyzed. 
//	 * @param reference Reference mapping.
//	 * @return
//	 */
//	private double getMeasure(Mapping a_mapping, Mapping reference, double crossProduct) {
//		PRFCalculator prfC = new PRFCalculator();
//		if(measure.equalsIgnoreCase(precision))
//			return prfC.precision(a_mapping, reference);
//		if(measure.equalsIgnoreCase(recall))
//			return prfC.recall(a_mapping, reference);
//		if(measure.equalsIgnoreCase(mcc))
//			return prfC.computeMatthewsCorrelation(a_mapping, reference, crossProduct);
//		return prfC.fScore(a_mapping, reference);
//	}
//	/**
//	 * Function to calculate fitness based on recall value.
//	 */
//	public void useRecall() {
//		measure = recall;
//	}
//	/**
//	 * Function to calculate fitness based on precision value.
//	 */
//	public void usePrecision() {
//		measure = precision;
//	}
//	/**
//	 * Function to calculate fitness based on f-score.
//	 */
//	public void useFScore() {
//		measure = fScore;
//	}
//	
//	/**
//	 * Uses Mathews Correlation Coefficient to calculate fitness.
//	 */
//	public void useMCC() {
//		measure = mcc;
//	}
//	/**
//	 * Implementing Singleton Pattern.
//	 * @param a_config A ExpresionConfiguration.
//	 * @param o Reference data.
//	 * @param measure Either "recall", "precision", or "f-score".
//	 * @return Instance of ExpressionFitnessFunction.
//	 */
//	public static ExpressionFitnessFunction getInstance(LinkSpecGeneticLearnerConfig a_config, Oracle o, String measure,int sampleSize) {
//		if(instance == null) {
//				instance = new ExpressionFitnessFunction(a_config, o, measure, sampleSize);
//		}
//		return instance;
//	}
//
//	public static ExpressionFitnessFunction getInstance(LinkSpecGeneticLearnerConfig a_config,  Mapping reference, String measure,int sampleSize) {
//		if(instance == null) {
//				instance = new ExpressionFitnessFunction(a_config, reference, measure, sampleSize);
//		}
//		return instance;
//	}
//	
////	/**
////	 * Method to get caches of the two knowledge bases.
////	 * @param discriminant Either "source" or "target".
////	 * @return HybridCache of the specified knowledge base.
////	 * @deprecated
////	 */
////	public HybridCache getCache(String discriminant) {
////		if(discriminant.equalsIgnoreCase("source")) {
////			return sC;
////		}
////		else {
////			return tC;
////		}
////	}
//	/**
//	 * Get full caches used by this learner.
//	 * @return HybridCache of the source endpoint.
//	 */
//	public Cache getSourceCache() {
//		return sC;
//	}
//	/**
//	 * Get full caches used by this learner.
//	 * @return HybridCache of the target endpoint.
//	 */
//	public Cache getTargetCache() {
//		return tC;
//	}
//	
//	public void destroy() {
//		instance = null;
//	}
//	
//	public void useMeasure(String name) {schrift
//		if(name.equalsIgnoreCase("recall"))
//			useRecall();
//		else	
//		if(name.equalsIgnoreCase("precision"))
//			usePrecision();
//		else
//			useFScore();
//	}
//	/**
//	 * Method to create the mapping based on the specified expression and acceptance threshold.
//	 * As of now this just wraps around the the SetConstraintsMapper getLinks() function.
//	 * @param expression LIMES metric expression used to compare instances of the source and target knowledgebases.
//	 * @param accThreshold global acceptance threshold.
//	 * @param full if set use full otherwise trimmed caches.
//	 * @return
//	 */
//	public Mapping getMapping(String expression, double accThreshold, boolean full) {
//		try {
//			if(full) {
////				logger.info("get full Mapping for "+expression+">="+accThreshold);
//				return sCMFull.getLinks(expression, accThreshold);
//			} else {
////				logger.info("get trimmed Mapping for "+expression+">="+accThreshold);
//				return sCM.getLinks(expression, accThreshold);
//			}
//		}catch(Exception e) {schrift
//			System.err.println("Exception execution expression "+expression+" <= "+accThreshold+ ". full? " + full);
//			return new MemoryMapping();
//		}
//		
//		catch(java.lang.OutOfMemoryError e) {
//			logger.warn("Out of memory trying to get Map for expression\""+expression+">="+accThreshold+"\".");
//			return new MemoryMapping();
//		}
//	}
//	
//	/**
//	 * Method to scale down caches according to given training data.
//	 * @param trainingData Mapping holding data instances a user has evaluated. That may include non-matches.
//	 */
//	public void trimKnowledgeBases(Mapping trainingData) {
//			this.trainingData = trainingData;
//			HybridCache[] trimmed = ExampleOracleTrimmer.processData(sC, tC, trainingData);
//			trimmedSourceCache = trimmed[0];
//			trimmedTargetCache = trimmed[1];		
//			logger.info("Trimming to "+trimmed[0].size()+" and "+trimmed[1].size()+" caches.");
//			crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
//			sCM = SetConstraintsMapperFactory.getMapper( "simple", m_config.source, m_config.target, 
//					trimmedSourceCache, trimmedTargetCache, f, 2);
//	}
//	
//	/**
//	 * As we assume referenceData only holding matches. Learner may have to set it separately.
//	 * @param referenceData A Mapping holding all matches.
//	 */
//	public void setReferenceMapping(Mapping referenceData) {
//		reference = referenceData;			
//	}
//	
//	/**
//	 * If we want to use Full Caches instead.
//	 * @param value
//	 */
//	public void useFullCaches(boolean value) {
//		if(value) {
//			sCM = SetConstraintsMapperFactory.getMapper( "simple", m_config.source, m_config.target, 
//					sC, tC, f, 2);
//			crossProduct = sC.size() * tC.size();
//		}
//		else {
//			sCM = SetConstraintsMapperFactory.getMapper( "simple", m_config.source, m_config.target, 
//					trimmedSourceCache, trimmedTargetCache, f, 2);
//			crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
//		}
//	}
//	
//	public Metric getMetric(IGPProgram p) {
//		Object[] args = {};
//		ProgramChromosome pc = p.getChromosome(0);
//		return (Metric) pc.getNode(0).execute_object(pc, 0, args);
//	}
//	public int getGranualarity() {
//		return granualarity;
//	}
//	public void setGranualarity(int granualarity) {
//		this.granualarity = granualarity;
//	}
//	
//	public Mapping getReferenceMapping() {
//		return reference;
//	}
//	
//	/**
//	 * Method to add instances to reference?.
//	 * @param m Mapping of matches, designated as such by an oracle.
//	 */
//	public void addToReference(Mapping m) {
//		logger.info("Filling reference of size "+reference.size()+" with "+m.size()+" additional matches.");
//		for(Entry<String, HashMap<String, Double>> e1 : m.map.entrySet()) {
//			for(Entry<String, Double> e2 : e1.getValue().entrySet()) {
//				reference.add(e1.getKey(), e2.getKey(), 1d);
//			}
//		}
//		logger.info("Reference has now "+reference.size()+" Matches.");
//	}
//	
//	public void fillCachesIncrementally(List<Triple> controversyMatches) {
//		for(Triple t : controversyMatches) {
//			if(!trimmedSourceCache.containsUri(t.getSourceUri())) {
//				logger.info("Adding instance "+t.getSourceUri()+" to sC");
//				trimmedSourceCache.addInstance(sC.getInstance(t.getSourceUri()));
//			}
//			if(!trimmedTargetCache.containsUri(t.getTargetUri())) {
//				logger.info("Adding instance "+t.getTargetUri()+" to tC");
//				trimmedTargetCache.addInstance(tC.getInstance(t.getTargetUri()));
//			}
//		}
//		sCM = SetConstraintsMapperFactory.getMapper( "simple", m_config.source, m_config.target, 
//				trimmedSourceCache, trimmedTargetCache, f, 2);
//		crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
//	}
//	schrift
//	public void setCaches(Cache sC, Cache tC) {
//		this.sC = sC;
//		this.tC = tC;
//	}
}
