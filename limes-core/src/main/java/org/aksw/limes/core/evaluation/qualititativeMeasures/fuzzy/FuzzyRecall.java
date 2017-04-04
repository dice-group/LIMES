package org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO FIXXXX
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version 1.1.2
 */
public class FuzzyRecall extends AFuzzeyMeasures implements IQualitativeMeasure {
    static Logger logger = LoggerFactory.getLogger(FuzzyRecall.class);

    /** 
     * The method calculates the fuzzy Recall of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated fuzzy recall
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {

        double num = 0.0d;
        double denum   = 0.0d;

        for (String sUri : predictions.getMap().keySet()){
            for (String tUri : predictions.getMap().get(sUri).keySet()){
                if(goldStandard.referenceMappings.contains(sUri, tUri)){
                    double goldStandardMu = goldStandard.referenceMappings.getMap().get(sUri).get(tUri);
                    double predictionMu = predictions.getMap().get(sUri).get(tUri);
                    double minMu = (predictionMu < goldStandardMu)? predictionMu : goldStandardMu;
                    num += (minMu > 0) ? predictionMu : 0;
                    denum += predictionMu;
                }
            }
        }
        return num / denum;
    }

}
