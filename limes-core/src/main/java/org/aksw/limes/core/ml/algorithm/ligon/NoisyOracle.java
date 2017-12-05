package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class NoisyOracle implements Oracle {

    protected AMapping oracleMap;
    ConfusionMatrix confusionMatrix = new ConfusionMatrix();


    public NoisyOracle(AMapping oracleMap, ConfusionMatrix confusionMatrix) {
        super();
        this.oracleMap = oracleMap;
        this.confusionMatrix = confusionMatrix;
    }

    public boolean predict(String subject, String object){
        boolean inOracle = oracleMap.contains(subject, object);

        Random random = new Random();
        if(inOracle){
            double probRightPos =  confusionMatrix.getTruePositiveProbability();
            double probWrongPos =  confusionMatrix.getFalsePositiveProbability();
            double minProb = (probRightPos < probWrongPos)? probRightPos : probWrongPos;
            double r = (probRightPos + probWrongPos) * random.nextDouble();
            if(r < minProb ){
                return (probRightPos < probWrongPos)? true : false;
            }else{
                return (probRightPos < probWrongPos)? false: true;
            }
        }else{
            double probRightNeg =  confusionMatrix.getTrueNegativeProbability();
            double probWrongNeg =  confusionMatrix.getFalseNegativeProbability();
            double minProb = (probRightNeg < probWrongNeg)? probRightNeg : probWrongNeg;
            double r = (probRightNeg + probWrongNeg) * random.nextDouble();
            if(r < minProb ){
                return (probRightNeg < probWrongNeg)? false : true;
            }else{
                return (probRightNeg < probWrongNeg)? true : false;
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
        return "\nNoisyOracle ["+ 
                confusionMatrix.getTruePositiveProbability() + ", " +
                confusionMatrix.getTrueNegativeProbability() + ", " +
                confusionMatrix.getFalsePositiveProbability() + ", " +
                confusionMatrix.getFalseNegativeProbability() +
                "]";
    }



}
