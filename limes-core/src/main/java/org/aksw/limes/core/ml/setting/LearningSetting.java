package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.oldalgorithm.IMLAlgorithm;
import org.apache.commons.collections15.map.HashedMap;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version May 17, 2016
 * @deprecated Use {@link}{@link LearningParameter} instead
 */
@Deprecated
public abstract class LearningSetting {

    protected static Logger logger = Logger.getLogger(LearningSetting.class.getName());

    protected Map<String, String> parameters = new HashedMap<>();
    // ---------------------- TO BE REMOVED ---------------------------------
    protected IMLAlgorithm algorithm;
    /**
     * maximal duration in seconds
     */
    protected long maxDuration = 60;
    int inquerySize = 10;
    /**
     * maximal number of iterations
     */
    int maxIteration = 500;
    /**
     * maximal quality in F-Measure // Pseudo-F. The implementing ML algorithm should it interpret as wished.
     */
    double maxQuality = 0.5;
    TerminationCriteria terminationCriteria = TerminationCriteria.iteration;
    double terminationCriteriaValue = 0;
    /**
     * beta for (pseudo) F-Measure
     */
    double beta = 1.0;
    // - EAGLE parameters
    int generations = 10; //FIXME use iterations?
    int population = 20;
    float mutationRate = 0.4f;
    float reproductionRate = 0.4f;
    float crossoverRate = 0.3f;
    boolean preserveFittest = true;
    // supervised
    IQualitativeMeasure measure = new FMeasure();
    //LION parameters
    double gammaScore = 0.15d;
    /**
     * Expansion penalty
     */
    double expansionPenalty = 0.7d;
    /**
     * reward for better then parent
     */
    double reward = 1.2;
    /**
     * switch pruning on /off
     */
    boolean prune = true;
    //	public double getBeta() {
//		return beta;
//	}
//	public void setBeta(double beta) {
//		this.beta = beta;
//	}
    PropertyMapping propMap = new PropertyMapping();
    public LearningSetting(IMLAlgorithm algorithm) {
        super();
        this.algorithm = algorithm;
    }

    String getParameterValue(String parameterName) {
        if (parameters.containsKey(parameterName)) {
            return parameters.get(parameterName);
        }
        logger.error("Parameter " + parameterName + " is not set. Exit with error!");
        System.exit(1);
        return null;
    }

    double getParameterDoubleValue(String parameterName) {
        if (parameters.containsKey(parameterName)) {
            return Double.parseDouble(parameters.get(parameterName));
        }
        logger.error("Parameter " + parameterName + " is not set. Exit with error!");
        System.exit(1);
        return -Double.MAX_VALUE;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getMaxQuality() {
        return maxQuality;
    }

    public void setMaxQuality(double maxQuality) {
        this.maxQuality = maxQuality;
    }

    public boolean isPrune() {
        return prune;
    }

    ;

    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
        terminationCriteriaValue = maxDuration;
    }

    public int getInquerySize() {
        return inquerySize;
    }

    public void setInquerySize(int inquerySize) {
        this.inquerySize = inquerySize;
    }

    public abstract void learn();

    public IMLAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public double getGammaScore() {
        return gammaScore;
    }

    public void setGammaScore(double gammaScore) {
        this.gammaScore = gammaScore;
    }

    public double getExpansionPenalty() {
        return expansionPenalty;
    }

    public void setExpansionPenalty(double expansionPenalty) {
        this.expansionPenalty = expansionPenalty;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public IQualitativeMeasure getMeasure() {
        return measure;
    }

    public void setMeasure(IQualitativeMeasure measure) {
        this.measure = measure;
    }

    public PropertyMapping getPropMap() {
        return propMap;
    }

    public void setPropMap(PropertyMapping propMap) {
        this.propMap = propMap;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
        terminationCriteriaValue = generations;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public float getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
    }

    public float getReproductionRate() {
        return reproductionRate;
    }

    public void setReproductionRate(float reproductionRate) {
        this.reproductionRate = reproductionRate;
    }

    public float getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(float crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public boolean isPreserveFittest() {
        return preserveFittest;
    }

    public void setPreserveFittest(boolean preserveFittest) {
        this.preserveFittest = preserveFittest;
    }

    public TerminationCriteria getTerminationCriteria() {
        return this.terminationCriteria;
    }

    public void setTerminationCriteria(TerminationCriteria criteria) {
        this.terminationCriteria = criteria;
    }

    /**
     * A convenient setter for different termination criteria. Will
     * interpret the given value depending on the criteria (e.g. as
     * maxDuration in seconds iff TerminationCriteria.duration is given.
     *
     * @param criteria
     *         Termination criteria
     * @param value
     *         Criteria specific value (seconds/number o
     */
    public boolean setTerminationCriteria(TerminationCriteria criteria, double value) {
        this.terminationCriteria = criteria;
        if (criteria == TerminationCriteria.duration) {
            this.maxDuration = (long) value;
            terminationCriteriaValue = value;
//			System.out.println("Setting duration based criteria to "+maxDuration+" seconds.");
            return true;
        }
        if (criteria == TerminationCriteria.iteration) {
            this.maxIteration = (int) value;
            terminationCriteriaValue = value;
//			System.out.println("Setting iteration based criteria to: "+maxIteration+" iterations");
            return true;
        }
        if (criteria == TerminationCriteria.quality) {
            this.maxQuality = value;
            terminationCriteriaValue = value;
//			System.out.println("Setting quality based criteria to: "+maxQuality+" quality");
            return true;
        }
        return false;
    }

    public double getTerminationCriteriaValue() {
        return terminationCriteriaValue;
    }

    /**
     * To differentiate termination criteria for ML algorithms.
     *
     * @author Klaus Lyko
     * @version Feb 10, 2016
     */
    public enum TerminationCriteria {
        iteration, // EAGLE generations others maxIteration
        duration, // using timeBased criteria, specified in millisecond: maxDuration
        quality, // a quality based approach
    }
}

