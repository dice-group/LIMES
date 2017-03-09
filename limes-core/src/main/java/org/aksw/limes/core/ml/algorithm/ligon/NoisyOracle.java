package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class NoisyOracle {

    AMapping oracleMap;
    double positiveTrustability;
    double negativeTrustability;

    NoisyOracle(){
        oracleMap = MappingFactory.createDefaultMapping();
        positiveTrustability = 0.5d;
        negativeTrustability = 0.5d;
    }

    
    public NoisyOracle(AMapping oracleMap, double positiveTrustability, double negativeTrustability) {
        super();
        this.oracleMap = oracleMap;
        this.positiveTrustability = positiveTrustability;
        this.negativeTrustability = negativeTrustability;
    }



    boolean predict(String subject, String object){
        boolean inOracle = oracleMap.contains(subject, object);
        Random random = new Random();
        if(inOracle){
            if(random.nextDouble() >= positiveTrustability){
                return inOracle;
            }else{
                return !inOracle;
            }
        }else{
            if(random.nextDouble() >= negativeTrustability){
                return inOracle;
            }else{
                return !inOracle;
            }
        }
    }
}
