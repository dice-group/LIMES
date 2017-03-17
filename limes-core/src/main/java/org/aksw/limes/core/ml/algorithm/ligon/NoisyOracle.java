package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class NoisyOracle {

    AMapping oracleMap;
    double tp, tn;
    double estimatedTp, estimatedTn;
    
    NoisyOracle(){
        oracleMap = MappingFactory.createDefaultMapping();
        tp = 0.5d;
        tn = 0.5d;
    }

    
    public NoisyOracle(AMapping oracleMap, double positiveTrustability, double negativeTrustability) {
        super();
        this.oracleMap = oracleMap;
        this.tp = positiveTrustability;
        this.tn = negativeTrustability;
    }



    boolean predict(String subject, String object){
        boolean inOracle = oracleMap.contains(subject, object);
        Random random = new Random();
        if(inOracle){
            return (random.nextDouble() >= tp) ? true : false;
        }else{
            return (random.nextDouble() >= tn) ? false: true ;
        }
    }



}
