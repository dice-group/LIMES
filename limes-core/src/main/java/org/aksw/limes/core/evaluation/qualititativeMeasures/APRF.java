/**
 *
 */
package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is an abstract class for the <b>Precision</b>, <b>Recall</b> and <b>F-Measure</b> classes.<br>
 * It contains set of methods that calculate the values of <b>true-positive</b>, <b>false-positive</b>, <b>true-negative</b> and <b>false-negative</b> which
 * are used by evaluators classes to evaluate the mappings results.<br>
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public abstract class APRF implements IQualitativeMeasure {
    static Logger logger = LoggerFactory.getLogger(APRF.class);

    /**
     * The method calculates either the true positive or the false positive results which are defined as true-positive: the results 
     * classified as positive and the classification is correct while false-positive: the results classified as positive and the classification is incorrect.
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @param truePositive A flag switches the calculation between true positive (=true) and false positive(=false)
     * @return double - This returns either True positive or False positive based on the flag value
     */
    public static double trueFalsePositive(final AMapping predictions, final AMapping goldStandard, boolean truePositive) {
        double TPCounter = 0, FPCounter = 0;
        for (String sUri : predictions.getMap().keySet()){
            for (String tUri : predictions.getMap().get(sUri).keySet()){
                if (goldStandard.contains(sUri, tUri) && goldStandard.getMap().get(sUri).get(tUri) > 0){
                    TPCounter++;
                } else if(!goldStandard.contains(sUri, tUri) && predictions.getMap().get(sUri).get(tUri) > 0){
                    FPCounter++;
                }
            }
        }
        if (truePositive)
            return TPCounter;
        return FPCounter;
    }

    /**
     * The method calculates the false-negative results such that the result is claimed by a machine learning as a negative one and the claim is false.<br>
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double This returns the number of false negative links
     */
    
    public static double falseNegative(final AMapping predictions, final AMapping goldStandard) {
        double FNCounter = 0;
        for (String sUri : goldStandard.getMap().keySet()){
            for (String tUri : goldStandard.getMap().get(sUri).keySet()){
                if (goldStandard.getMap().get(sUri).get(tUri) > 0 && (!(predictions.contains(sUri, tUri) && predictions.getMap().get(sUri).get(tUri) > 0))){
                        FNCounter++;
                }
            }
        }
        return FNCounter;
    }

    public static double trueNegative(AMapping predictions, GoldStandard goldStandard) {
//    	//These is the size of the actual negatives
        double negativesSize = (goldStandard.sourceUris.size() * goldStandard.targetUris.size()) - predictions.size();
        for (String sUri : predictions.getMap().keySet()){
            for (String tUri : predictions.getMap().get(sUri).keySet()){
                if (predictions.contains(sUri, tUri) && predictions.getMap().get(sUri).get(tUri) <= 0){
                	negativesSize++;
                }
            }
        }
        return negativesSize - falseNegative(predictions, goldStandard.referenceMappings); 
    }
    /** 
     * The Abstract method to be implemented for calculating the accuracy of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated accuracy
     */
    public abstract double calculate(AMapping predictions, GoldStandard goldStandard);
}
