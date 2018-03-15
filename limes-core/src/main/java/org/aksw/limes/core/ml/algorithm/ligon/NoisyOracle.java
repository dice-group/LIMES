package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

/**
 * @author mohamedsherif
 *
 */
public class NoisyOracle implements Oracle {

    protected AMapping oracleMap;
    protected ConfusionMatrix confusionMatrix = new ConfusionMatrix();

    public NoisyOracle(AMapping oracleMap, ConfusionMatrix confusionMatrix) {
        super();
        this.oracleMap = oracleMap;
        this.confusionMatrix = confusionMatrix;
    }
    
    public NoisyOracle() {
    		this(null, new ConfusionMatrix());
    }

    public boolean predict(String subject, String object){
        boolean inOracle = oracleMap.contains(subject, object);

        Random random = new Random();
        if(inOracle){
            double probTruePos  = confusionMatrix.getTruePositiveProbability();
            double probFalsePos = confusionMatrix.getFalsePositiveProbability();
            double minProb = (probTruePos < probFalsePos) ? probTruePos : probFalsePos;
            double r = (probTruePos + probFalsePos) * random.nextDouble();
            if(r < minProb){
                return (probTruePos < probFalsePos) ? true : false;
            }else{
                return (probTruePos < probFalsePos) ? false: true;
            }
        }else{
            double probTrueNeg  = confusionMatrix.getTrueNegativeProbability();
            double probFalseNeg = confusionMatrix.getFalseNegativeProbability();
            double minProb = (probTrueNeg < probFalseNeg) ? probTrueNeg : probFalseNeg;
            double r = (probTrueNeg + probFalseNeg) * random.nextDouble();
            if(r < minProb){
                return (probTrueNeg < probFalseNeg) ? false : true;
            }else{
                return (probTrueNeg < probFalseNeg) ? true : false;
            }
        }
    }

    public AMapping getOracleMap() {
        return oracleMap;
    }


    public void setOracleMap(AMapping oracleMap) {
        this.oracleMap = oracleMap;
    }

    @Override
    public String toString() {
        return "\nNoisyOracle "+ confusionMatrix.characteristicMatrixToString();
    }



}
