package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class NoisyOracle {

    protected AMapping oracleMap;
    ConfusionMatricex confusionMatrix = new ConfusionMatricex();




    boolean predict(String subject, String object){
        boolean inOracle = oracleMap.contains(subject, object);

        Random random = new Random();
        if(inOracle){
            double probRightPos =  confusionMatrix.getProbabilityOfRightClassifiedPositiveExamples();
            double probWrongPos =  confusionMatrix.getProbabilityOfRightClassifiedPositiveExamples();
            double minProb = (probRightPos < probWrongPos)? probRightPos : probWrongPos;
            double r = (probRightPos + probWrongPos) * random.nextDouble();
            if(r < minProb ){
                return (probRightPos < probWrongPos)? true : false;
            }else{
                return (probRightPos < probWrongPos)? false:true;
            }
        }else{
            double probRightNeg =  confusionMatrix.getProbabilityOfRightClassifiedNegativeExamples();
            double probWrongNeg =  confusionMatrix.getProbabilityOfWrongClassifiednegativeExamples();
            double minProb = (probRightNeg < probWrongNeg)? probRightNeg : probWrongNeg;
            double r = (probRightNeg + probWrongNeg) * random.nextDouble();
            if(r < minProb ){
                return (probRightNeg < probWrongNeg)? true : false;
            }else{
                return (probRightNeg < probWrongNeg)? false:true;
            }
        }
    }

    public AMapping getOracleMap() {
        return oracleMap;
    }


    public void setOracleMap(AMapping oracleMap) {
        this.oracleMap = oracleMap;
    }
    
}
