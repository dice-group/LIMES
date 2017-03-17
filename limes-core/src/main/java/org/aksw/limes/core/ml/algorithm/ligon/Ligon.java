package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ligon {
    static Logger logger = LoggerFactory.getLogger(Ligon.class);

    double TAU = 1.0;
    AMapping posMap, negMap, unknownMap; 

    List<NoisyOracle> noisyOracles;
    List<Double> oraclesTrust;

    public Ligon(int nrOfOracles){
        super();
        posMap = MappingFactory.createDefaultMapping();
        negMap = MappingFactory.createDefaultMapping();
        unknownMap = MappingFactory.createDefaultMapping();
        noisyOracles = new ArrayList<>();
        oraclesTrust = new ArrayList<>();
    }



    public Ligon(double tAU, AMapping posMap, AMapping negMap,
            AMapping unknownMap, List<NoisyOracle> noisyOracles,
            List<Double> oraclesTrust) {
        super();
        TAU = tAU;
        this.posMap = posMap;
        this.negMap = negMap;
        this.unknownMap = unknownMap;
        this.noisyOracles = noisyOracles;
        EstimateOraclesTrust(posMap, negMap);
    }


    void EstimateOraclesTrust(AMapping posMap, AMapping negMap){

        // Positive training data
        for(NoisyOracle noisyOracle: noisyOracles){
            int tp = 0;
            for (String s : posMap.getMap().keySet()) {
                for (String t : posMap.getMap().get(s).keySet()) {
                    tp += (noisyOracle.predict(s, t)) ? 1 : 0 ;
                    noisyOracle.estimatedTp = tp / posMap.size();
                }
            }
        }

        // Negative training data
        for(NoisyOracle noisyOracle: noisyOracles){
            int tn = 0;
            for (String s : negMap.getMap().keySet()) {
                for (String t : posMap.getMap().get(s).keySet()) {
                    tn += (!noisyOracle.predict(s, t)) ? 1 : 0 ;
                    noisyOracle.estimatedTp = tn / negMap.size();
                }
            }
        }
    }


    void updateTrainingData(AMapping unlabeledMap){
        for (String s : unlabeledMap.getMap().keySet()) {
            for (String t : unlabeledMap.getMap().get(s).keySet()) {
                double pTrue = estimateTrue(s,t);
                double pFalse = estimateFalse(s,t);
                if(pTrue >= TAU * pFalse){
                    posMap.add(s, t, 1.0);
                }else{
                    negMap.add(s, t, 1.0);
                }
            }
        }
    }
    
    
    double estimateTrue(String subject, String object){
        double result = 1; 
        for(NoisyOracle noisyOracle: noisyOracles){
            if(noisyOracle.predict(subject, object)){
                result *= 1 - ((noisyOracle.predict(subject, object))? noisyOracle.estimatedTp: (1 - noisyOracle.estimatedTp));
            }
        }
        return 1 - result;
    }

    
    double estimateFalse(String subject, String object){
        double result = 1; 
        for(NoisyOracle noisyOracle: noisyOracles){
            if(noisyOracle.predict(subject, object)){
                result *= 1 - ((noisyOracle.predict(subject, object))? noisyOracle.estimatedTn: (1 - noisyOracle.estimatedTn));
            }
        }
        return 1 - result;
    }

    public static void main(String args[]){

    }
}
