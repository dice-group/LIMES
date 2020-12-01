package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.BranchTypingCross;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.TournamentSelector;


/**
 * Configure JGAP evolutionary process to be used by genetic link specification leaner.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class LinkSpecGeneticLearnerConfig extends GPConfiguration {
    /**
     *
     */
    private static final long serialVersionUID = 2787442443170665028L;
    public KBInfo source;
    public KBInfo target;
    public ACache sC;
    public ACache tC;
    PropertyMapping propMap;
    int granularity = 2;

    /**
     * Constructor.
     *
     * @param KBIsource
     *         Specifies the source knowledge base.
     * @param KBItarget
     *         Specifies the target knowledge base.
     * @param propMapping
     *         A Mapping of Properties of both source and target.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public LinkSpecGeneticLearnerConfig(KBInfo KBIsource, KBInfo KBItarget, PropertyMapping propMapping) throws InvalidConfigurationException {
        super();
        this.source = KBIsource;
        this.target = KBItarget;
        this.propMap = propMapping;
        setGPFitnessEvaluator(new DeltaGPFitnessEvaluator()); // the lower the better
        setCrossoverMethod(new BranchTypingCross(this)); // standard
        setSelectionMethod(new TournamentSelector(2)); // new TournamentSelector(2) vs. FitnessProportionate
        setProgramCreationMaxTries(100);
        setMinInitDepth(1);
        setMaxInitDepth(15);
        setPreservFittestIndividual(true);
        setNewChromsPercent(0.25f); // 0.4
        setMaxCrossoverDepth(5);
        setVerifyPrograms(false);
        // seems that if we use program caches it only verifies if the first chromosome has changed.
        setUseProgramCache(false);
        setAlwaysCaculateFitness(true);
        //defaults
        this.setMutationProb(0.4f);
        this.setCrossoverProb(0.4f);
        //   this.setReproductionProb(0.7f);
        this.setPopulationSize(30);
    }


    /**
     * Little helper function to retrieve plane property names.
     *
     * @param id
     *         Number of the property in the property List.
     * @param discriminant
     *         String to differ between the two Linked Data Sources dubbed "source" and "target".
     * @return name of the property with the given number.
     */
    public String getPropertyName(String discriminant, int id) {
        String ret = "";
        if (discriminant.equalsIgnoreCase("source"))
            ret = source.getProperties().get(id);
        else
            ret = target.getProperties().get(id);
        if (ret.contains(".")) {
            if (ret.split("\\.").length == 2) {
                ret = ret.split("\\.")[1];
            }
        }
        return ret;
    }

    /**
     * Function to retrieve properties of the Linked Data sources dubbed source and target
     * completed with the specified variable of the knowledge base to comply with the limes dtd.
     * Variables have in most cases the form "?x", properties are the names of the property possibly starting
     * with a namespace, e.g. "dc:title". But for a metric expression we have to combine both without the question mark:
     * E.g. "x.dc:title".
     *
     * @param discriminant
     *         String "source" or "target" to differ between the both knowledge bases.
     * @param id
     *         Number of the property in the properties list in the according KBInfo.
     * @return Combined String of variable and property.
     */
    public String getExpressionProperty(String discriminant, int id) {
        return getExpressionProperty(discriminant, getPropertyName(discriminant, id));
    }

    public String getExpressionProperty(String discriminant, String propName) {
        String ret;
        if (discriminant.equalsIgnoreCase("source")) {
            ret = source.getVar();
            if (ret.startsWith("?") && ret.length() >= 2)
                ret = ret.substring(1);
            ret += "." + propName;
            return ret;
        } else {
            ret = target.getVar();
            if (ret.startsWith("?") && ret.length() >= 2)
                ret = ret.substring(1);
            ret += "." + propName;
            return ret;
        }
    }


//	/**
//	 * Returns the bounds of the indices of properties which have numeric values. 
//	 * Notice that all date properties must be specified at the end of the
//	 * property list!
//	 * @param discriminant String to separate knowledge bases: "source" or "target"
//	 * @return Array of indices, first element is the lower bound, second the upper bound.
//	 */
//	public int[] getDatePropertyIndices(String discriminant) {
//		int[] answer = new int[2];
//		if(discriminant.equalsIgnoreCase("source")) {
//			answer[0] = propMap.sourceStringProps.size() + propMap.sourceNumberProps.size();
//			answer[1] = propMap.sourceStringProps.size() 
//					+ propMap.sourceNumberProps.size()
//					+ propMap.sourceNumberProps.size()-1;
//		}
//		else {
//			answer[0] = propMap.targetStringProps.size() + propMap.sourceNumberProps.size();
//			answer[1] = propMap.targetStringProps.size()
//					+ propMap.sourceNumberProps.size()
//					+ propMap.targetNumberProps.size()-1;
//		}
//		return answer;
//	}


//	/**
//	 * Returns the bounds of the indices of properties which have numeric values. 
//	 * Notice that all numeric properties must be specified at the end of the
//	 * property list!
//	 * @param discriminant String to separate knowledge bases: "source" or "target"
//	 * @return Array of indices, first element is the lower bound, second the upper bound.
//	 */
//	public int[] getNumberPropertyIndices(String discriminant) {
//		int[] answer = new int[2];
//		if(discriminant.equalsIgnoreCase("source")) {
//			answer[0] = propMap.sourceStringProps.size();
//			answer[1] = propMap.sourceStringProps.size() + propMap.sourceNumberProps.size()-1;
//		}
//		else {
//			answer[0] = propMap.targetStringProps.size();
//			answer[1] = propMap.targetStringProps.size() + propMap.targetNumberProps.size()-1;
//		}
//		return answer;
//	}

//	/**
//	 * Get indices representing String properties of the knowledge bases. 
//	 * @param discriminant Either "source" or "target".
//	 * @return Array holding two Integer. The first is the start index, the second entry the last index.
//	 */
//	public int[] getStringPropertyIndices(String discriminant) {
//		int[] answer = new int[2];
//		if(discriminant.equalsIgnoreCase("source")) {
//			answer[0] = 0;
//			answer[1] = propMap.sourceStringProps.size()-1;
//		}
//		else {
//			answer[0] = 0;
//			answer[1] = propMap.targetStringProps.size()-1;
//		}
//		return answer;
//	}

    /**
     * Do we have to consider numeric properties?
     *
     * @return True if the given PropertyMapping consists of a mapping of two numeric properties.
     */
    public boolean hasNumericProperties() {
        if (propMap.getNumberPropMapping().size() > 0)
            return true;
        return false;
    }
    
    /**
     * Do we have to consider date properties?
     *
     * @return true iff the given PropertyMapping states a mapping between two date propeties.
     */
    public boolean hasDateProperties() {
        if (propMap.getDatePropMapping().size() > 0)
            return true;
        return false;
    }

    /**
     * Do we have to consider pointset properties?
     *
     * @return true iff the given PropertyMapping states a mapping between two date propeties.
     */
    public boolean hasPointSetProperties() {
        if (propMap.getPointsetPropMapping().size() > 0)
            return true;
        return false;
    }

    /**
     * In case we don't want to.
     *
     * @return
     */
    protected boolean redundantCommands() {
        return true;
    }

    public boolean isPropertyMatch(int sourceIndex, int targetIndex) {
        return propMap.isMatch(getPropertyName("source", sourceIndex),
                getPropertyName("target", targetIndex));
    }

    public PropertyMapping getPropertyMapping() {
        return this.propMap;
    }

    public void setPropertyMapping(PropertyMapping propMap) {
        this.propMap = propMap;
    }
}
