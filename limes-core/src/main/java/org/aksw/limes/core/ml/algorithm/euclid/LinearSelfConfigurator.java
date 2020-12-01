package org.aksw.limes.core.ml.algorithm.euclid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.controller.LSPipeline;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public class LinearSelfConfigurator implements ISelfConfigurator {
	// execution mode. STRICT = true leads to a strong bias towards precision by
	// ensuring that the initial classifiers are classifiers that have the
	// maximal threshold that leads to the best pseudo-f-measure. False leads to the
	// best classifier with the smallest threshold
	public boolean STRICT = true;
	public int ITERATIONS_MAX = 1000;
	public double MIN_THRESHOLD = 0.3;
	
    static Logger logger = LoggerFactory.getLogger(LinearSelfConfigurator.class);

    public enum Strategy {
        FMEASURE, THRESHOLD, PRECISION, RECALL
    };

    public enum QMeasureType {
        SUPERVISED, UNSUPERVISED
    }


    
    Strategy strategy = Strategy.FMEASURE;
    QMeasureType qMeasureType = QMeasureType.UNSUPERVISED;
    public ACache source; //source cache
    public ACache target; //target cache
    Map<String, Double> sourcePropertiesCoverageMap; //coverage map for latter computations
    Map<String, Double> targetPropertiesCoverageMap;//coverage map for latter computations
    List<SimpleClassifier> buffer; //buffers learning results
    double beta = 1;
    Map<String, String> measures = new HashMap<>();;
    public double learningRate = 0.25;
    public double kappa = 0.9;
    public double min_coverage = 0.9;
    /* used to compute qualities for the unsupervised approach*/
    private IQualitativeMeasure qMeasure = null;
    /* supervised approaches need a reference mapping to compute qualities*/
    AMapping reference = MappingFactory.createDefaultMapping(); // all true instance pairs.
    public AMapping asked = MappingFactory.createDefaultMapping();// all known instance pairs.
    
    /**
     * Set PFMs based upon name.
     * if name.equals("reference") using ReferencePseudoMeasures.class:  Nikolov/D'Aquin/Motta ESWC 2012.
     */
    public void setPFMType(QMeasureType qMeasureType) {
        this.qMeasureType = qMeasureType;
        switch (qMeasureType) {
            case SUPERVISED:
                qMeasure = EvaluatorFactory.create(EvaluatorType.F_MEASURE);
                break;
            case UNSUPERVISED:
            	qMeasure = EvaluatorFactory.create(EvaluatorType.PF_MEASURE);
                break;
        }
    }

    /**
     * Set PFMs based upon name.
     * if name.equals("reference") using ReferencePseudoMeasures.class:  Nikolov/D'Aquin/Motta ESWC 2012.
     */
    public void setMeasure(IQualitativeMeasure pfm) {
        this.qMeasureType = QMeasureType.UNSUPERVISED;
        this.qMeasure = pfm;
    }
    
    /**
     * Constructor
     *
     * @param source Source cache
     * @param target Target cache
     */
    public LinearSelfConfigurator(ACache source, ACache target) {
    	this(source, target, 0.9, 1);
    }

    /**
     * Constructor
     *
     * @param source Source cache
     * @param target Target cache
     * @param minCoverage Minimal coverage for a property to be considered for linking
     * @param beta Beta value for computing F_beta
     * @param measures Atomic measures
     */
    public LinearSelfConfigurator(ACache source, ACache target, double minCoverage, double beta, Map<String, String> measures) {
    	this(source, target, minCoverage, beta);
        this.measures = measures;
    }
    
    /**
     * Constructor
     *
     * @param source Source cache
     * @param target Target cache
     * @param beta Beta value for computing F_beta
     * @param minCoverage Minimal coverage for a property to be considered for
     * linking
     *
     */
    public LinearSelfConfigurator(ACache source, ACache target, double minCoverage, double beta) {
    	this.source = source;
    	this.target = target;
        this.beta = beta;
        sourcePropertiesCoverageMap = getPropertyStats(source, minCoverage);
        targetPropertiesCoverageMap = getPropertyStats(target, minCoverage);
        setPFMType(this.qMeasureType);
        setDefaultMeasures();
    }

    /**
     * set default atomic measures  
     */
    public void setDefaultMeasures() {
        measures = new HashMap<>();
        measures.put("euclidean", "numeric");
        measures.put("levenshtein", "string");
        measures.put("jaccard", "string");
        measures.put("trigrams", "string");
    }

    public void computeMeasure(ACache source, ACache target, String[] parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public String getThreshold() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AMapping getResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    /**
     * Extracts all properties from a cache that have a coverage beyond minCoverage
     *
     * @param c Input cache
     * @param minCoverage Threshold for coverage
     * @return Map of property to coverage
     */
    public static Map<String, Double> getPropertyStats(ACache c, double minCoverage) {
        Map<String, Double> buffer = new HashMap<>();
        Map<String, Double> result = new HashMap<>();

        //first count how often properties appear across instances
        for (Instance i : c.getAllInstances()) {
            for (String p : i.getAllProperties()) {
                if(p.equalsIgnoreCase("price") || p.equalsIgnoreCase("year"))
                    continue;
                if (!buffer.containsKey(p)) {
                    buffer.put(p, 1.0);
                } else {
                    buffer.put(p, buffer.get(p) + 1);
                }
            }
        }

        //then compute their coverage
        double total = (double) c.getAllInstances().size();
        double coverage;
        for (String p : buffer.keySet()) {
            coverage = (double) buffer.get(p) / total;
            if (coverage >= minCoverage) {
                result.put(p, coverage);
            }
        }
        return result;
    }

    @Override
    public String getMeasure() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMeasure(AMeasure measure) {
        // TODO Auto-generated method stub

    }

    public static String getPropertyType(ACache c, String p) {
        for (Instance i : c.getAllInstances()) {
            if (i.getAllProperties().contains(p)) {
                for (String value : i.getProperty(p)) {
                    if (!value.matches("[0-9]*\\.*[0-9]*")) {
                        return "string";
                    }
                }
            }
        }
        return "numeric";
    }

    /**
     * Computes all initial classifiers that compare properties whose coverage
     * is beyong the coverage threshold
     *
     * @return A map of sourceProperty to targetProperty to Classifier
     */
    public List<SimpleClassifier> getAllInitialClassifiers() {
        List<SimpleClassifier> initialClassifiers = new ArrayList<SimpleClassifier>();
        for (String p : sourcePropertiesCoverageMap.keySet()) {
            for (String q : sourcePropertiesCoverageMap.keySet()) {
                SimpleClassifier cp = getInitialClassifier(p, q, "jaccard");
                initialClassifiers.add(cp);
            }
        }
        return initialClassifiers;
    }

    /**
     * Computes the best initial mapping for each source property
     *
     * @return List of classifiers that each contain the best initial mappings
     */
    public List<SimpleClassifier> getBestInitialClassifiers() {
        Set<String> measureList = new HashSet<String>();
        measureList.add(MeasureFactory.COSINE);
   		measureList.add(MeasureFactory.LEVENSHTEIN);
        measureList.add(MeasureFactory.TRIGRAM);
        return getBestInitialClassifiers(measureList);
    }

    /**
     * Computes the best initial mapping for each source property
     * @param measureList Define Measures to be used by their name: eg. "jaccard", "levenshtein", "trigrams", "cosine", ...
     * @return List of classifiers that each contain the best initial mappings
     */
    public List<SimpleClassifier> getBestInitialClassifiers(Set<String> measureList) {
        List<SimpleClassifier> initialClassifiers = new ArrayList<>();
        //        logger.info(sourcePropertiesCoverageMap);
        //        logger.info(targetPropertiesCoverageMap);
        for (String p : sourcePropertiesCoverageMap.keySet()) {
            double fMeasure = 0;
            SimpleClassifier bestClassifier = null;
            //String bestProperty = "";
//            Map<String, SimpleClassifier> cp = new HashMap<>();
            for (String q : targetPropertiesCoverageMap.keySet()) {
                for (String measure : measureList) {
                    SimpleClassifier cps = getInitialClassifier(p, q, measure);
                    if (cps.getfMeasure() > fMeasure) {
                        bestClassifier = cps.clone();
                        //bestProperty = q;
                        fMeasure = cps.getfMeasure();
                    }
                }
            }
            if (bestClassifier != null) {
                initialClassifiers.add(bestClassifier);
            }
        }
        return initialClassifiers;
    }
    /**
     * Gets the best parameter to match the entities contained in the source and
     * target via properties p1 and p2 by the means of the similarity measure
     * "measure"
     *
     * @param sourceProperty Source property
     * @param targetProperty Target property
     * @param measure Similarity measure to be used
     * @return Maximal threshold that leads to maximal f-Measure (bias towards
     * precision)
     */
    private SimpleClassifier getInitialClassifier(String sourceProperty, String targetProperty, String measure) {
        double fMax = 0;
        double theta = 1.0;
        for (double threshold = 1; threshold > MIN_THRESHOLD; threshold = threshold - learningRate) {
//        	logger.info("execute("+sourceProperty+", "+targetProperty+ ", "+measure+", "+threshold+");");
            AMapping mapping = execute(sourceProperty, targetProperty, measure, threshold);
            //            double fMeasure = qMeasure.calculate(source.getAllUris(), target.getAllUris(), mapping, beta);
            double fMeasure = computeQuality(mapping);
            //            System.out.println("Source: " + sourceProperty + ""
            //                    + " Target: " + targetProperty + " Threshold " + threshold + " leads to F-Measure " + fMeasure);

            if (STRICT) {
                if (fMeasure > fMax) {
                    fMax = fMeasure;
                    theta = threshold;
                }
            } else {
                if (fMeasure >= fMax) {
                    fMax = fMeasure;
                    theta = threshold;
                }
            }
            if (fMeasure < fMax) {
                break;
            }
        }

        SimpleClassifier cp = new SimpleClassifier(measure, theta);
        cp.setfMeasure(fMax);
        cp.setSourceProperty(sourceProperty);
        cp.setTargetProperty(targetProperty);
        return cp;
    }

    /**
     * Runs classifiers and retrieves the corresponding mappings
     *
     * @param classifiers List of classifiers
     * @return AMapping generated by the list of classifiers
     */
    public AMapping getMapping(List<SimpleClassifier> classifiers) {
        classifiers = normalizeClassifiers(classifiers);
        Map<SimpleClassifier, AMapping> mappings = new HashMap<SimpleClassifier, AMapping>();
        for (int i = 0; i < classifiers.size(); i++) {
            double threshold = 1 + classifiers.get(i).getWeight() - (1 / kappa);
            if (threshold > 0) {
                AMapping m = executeClassifier(classifiers.get(i), threshold);
                mappings.put(classifiers.get(i), m);
            }
        }
        return getOverallMapping(mappings, 1.0);
    }

    /**
     * Computes the weighted linear combination of the similarity computed by
     * the single classifiers
     *
     * @param mappings Maps classifiers to their results
     * @param threshold Similarity threshold for exclusion
     * @return Resulting overall mapping
     */
    public AMapping getOverallMapping(Map<SimpleClassifier, AMapping> mappings, double threshold) {
        if (mappings.isEmpty()) {
            return MappingFactory.createDefaultMapping();
        }
        AMapping reference = mappings.get(mappings.keySet().iterator().next());
        AMapping result = MappingFactory.createDefaultMapping();
        for (String s : reference.getMap().keySet()) {
            for (String t : reference.getMap().get(s).keySet()) {
                double score = 0;
                for (SimpleClassifier cp : mappings.keySet()) {
                    score = score + cp.getWeight() * mappings.get(cp).getConfidence(s, t);
                }
                if (score >= threshold) {
                    result.add(s, t, score);
                }
            }
        }
        return result;
    }

    public List<SimpleClassifier> normalizeClassifiers(List<SimpleClassifier> classifiers) //weight classifier by f-measure. Assume that overall theta is 1
    {
        double total = 0;
        for (SimpleClassifier cp : classifiers) {
            total = total + cp.getWeight();
        }
        for (SimpleClassifier cp : classifiers) {
            cp.setWeight( cp.getWeight() / (total * kappa));
        }
        return classifiers;
    }

    /**
     * Updates the weights of the classifiers such that they map the initial
     * conditions for a classifier
     *
     * @param classifiers Input classifiers
     * @return Normed classifiers
     */
    public List<SimpleClassifier> getInitialOverallClassifiers(List<SimpleClassifier> classifiers) {
        //weight classifier by f-measure. Assume that overall theta is 1
        if (strategy.equals(Strategy.FMEASURE)) {
            double total = 0;
            for (SimpleClassifier cp : classifiers) {
                total = total + cp.getfMeasure();
            }
            for (SimpleClassifier cp : classifiers) {
                cp.setWeight( cp.getfMeasure() / (total * kappa));
            }
        }
        return classifiers;    }

    /**
     * Aims to improve upon a particular classifier by checking whether adding a
     * delta to its similarity worsens the total classifer
     *
     **/

    public double computeNext(List<SimpleClassifier> classifiers, int index) {
    	// FIXME simply subtracting learning rate?
    	double newWeight = Math.max(0, classifiers.get(index).getWeight() - learningRate);
    	if(newWeight == classifiers.get(index).getWeight()) {
    		logger.info("Computed weight under zero. Skipping.");
    	} else {
            classifiers.get(index).setWeight(newWeight);
            classifiers = normalizeClassifiers(classifiers);
    	}
        AMapping m = getMapping(classifiers);
        buffer = classifiers;

        return	computeQuality(m);
    }

    public List<SimpleClassifier> learnClassifer(List<SimpleClassifier> classifiers) {
        classifiers = normalizeClassifiers(classifiers);
        AMapping m = getMapping(classifiers);
        //      double f = qMeasure.calculate(source.getAllUris(), target.getAllUris(), m, beta);
        double f = computeQuality(m);
        // no need to update if the classifiers are already perfect
        if (f == 1.0) {
            return classifiers;
        }
        int dimensions = classifiers.size();
        int direction = 0;
        int iterations = 0;
        List<SimpleClassifier> bestClassifiers = null;
        double bestF = f;

        while (iterations <= ITERATIONS_MAX) {
            iterations++;
            double fMeasure;
//            double index = -1;
            //evaluate neighbors of current classifier
            for (int i = 0; i < dimensions; i++) {
                fMeasure = computeNext(classifiers, i);
                if (fMeasure > bestF) {
                    bestF = fMeasure;
//                    index = i;
                    bestClassifiers = buffer;
                }
            }
            //nothing better found. simply march in the space in direction
            //"direction"
            if (bestF == f) {
                logger.info(">>>> Walking along direction " + direction);
                computeNext(classifiers, direction);
                bestClassifiers = buffer;
                direction++;
                direction = direction % dimensions;
            } //every classifier is worse
            else if (bestF < f) {
                return bestClassifiers;
            } //found a better classifier
            classifiers = bestClassifiers;
            logger.info(">> Iteration " + iterations + ": " + classifiers + " F-Measure = " + bestF);
        }
        return classifiers;
    }


    /**
     * Runs a classifier and get the mappings for it
     *
     * @param c Classifier
     * @param threshold Threshold for similarities
     * @return Corresponding mapping
     */
    public AMapping executeClassifier(SimpleClassifier c, double threshold) {
        return execute(c.getSourceProperty(), c.getTargetProperty(), c.getMeasure(), threshold);
    }

    /**
     * Runs measure(sourceProperty, targetProperty) &gt;= threshold
     *
     * @param sourceProperty Source property
     * @param targetProperty Target property
     * @param measure Similarity measure
     * @param threshold Similarity threshold
     * @return Correspoding AMapping
     */
    public AMapping execute(String sourceProperty, String targetProperty, String measure, double threshold) {
        String measureExpression = measure + "(x." + sourceProperty + ", y." + targetProperty + ")";
        return LSPipeline.execute(source, target, new LinkSpecification(measureExpression, threshold));
    }

    /**
     * Gets the best target for each source and returns it
     *
     * @param m
     * @return
     */
    public AMapping getBestOneToOneMapping(AMapping m) {
        AMapping result = MappingFactory.createDefaultMapping();
        for (String s : m.getMap().keySet()) {
            double maxSim = 0;
            Set<String> target = new HashSet<String>();;
            for (String t : m.getMap().get(s).keySet()) {
                if (m.getConfidence(s, t) == maxSim) {
                    target.add(t);
                }
                if (m.getConfidence(s, t) > maxSim) {
                    maxSim = m.getConfidence(s, t);
                    target = new HashSet<String>();
                    target.add(t);
                }
            }
            for (String t : target) {
                result.add(s, t, maxSim);
            }
        }
        return result;
    }

    /**
     * Method to compute quality of a mapping. Uses per default the specified PFM qMeasure.
     * TODO: active learning variant.
     * @param map
     * @return
     */
    public Double computeQuality(AMapping map) {
    	return qMeasure.calculate(map, new GoldStandard(reference, source.getAllUris(), target.getAllUris()));
    }

    /** Set caches to trimmed caches according to the given reference mapping.
     * @param reference
     */
    public void setSupervisedBatch(AMapping reference) {
    	logger.info("Setting training data to "+reference.size()+" links");
        this.qMeasureType = QMeasureType.SUPERVISED;
        setPFMType(this.qMeasureType);
        for(String sUri : reference.getMap().keySet()) {
            for(String tUri : reference.getMap().get(sUri).keySet()) {
                double sim = reference.getConfidence(sUri, tUri);
                if(sim > 0)
                    this.reference.add(sUri, tUri, sim);
                this.asked.add(sUri, tUri, sim);
            }
        }
    }

    public AMapping minimizeToKnow(AMapping map) {
        AMapping minimal = MappingFactory.createDefaultMapping();
        for(String sUri : map.getMap().keySet()) {
            for(String tUri : map.getMap().get(sUri).keySet()) {
                if(asked.getMap().containsKey(sUri)) {
                    //is also tUri known?
                    for(String knownSuri : asked.getMap().keySet())
                        if(asked.getMap().get(knownSuri).containsKey(tUri))
                            minimal.add(sUri, tUri, map.getConfidence(sUri, tUri));
                }
            }
        }
        return minimal;
    }

    public ACache getSource() {
        return source;
    }

    public void setSource(ACache source) {
        this.source = source;
    }

    public ACache getTarget() {
        return target;
    }

    public void setTarget(ACache target) {
        this.target = target;
    }
    
    /**
     * TODO FIXME this is only a basic implementation
     * @param list
     * @return
     */
    public LinkSpecification getLinkSpecification(List<SimpleClassifier> list) {
    	LinkSpecification parent = new LinkSpecification();
    	// TODO apply linear weights
    	for(SimpleClassifier sc : list) {
    		LinkSpecification child = new LinkSpecification();
    		child.readSpec(sc.getMetricExpression(), sc.getThreshold());
    		child.setParent(parent);
    		parent.addChild(child);
    	}
    	return parent;
    }


}
