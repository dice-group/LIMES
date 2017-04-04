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
    protected AMapping posMap, negMap, unknownMap; 

    List<NoisyOracle> noisyOracles;

    public Ligon(int nrOfOracles){
        super();
        posMap = MappingFactory.createDefaultMapping();
        negMap = MappingFactory.createDefaultMapping();
        unknownMap = MappingFactory.createDefaultMapping();
        noisyOracles = new ArrayList<>();
    }



    public Ligon(AMapping posMap, AMapping negMap,
            AMapping unknownMap, List<NoisyOracle> noisyOracles) {
        super();
        this.posMap = posMap;
        this.negMap = negMap;
        this.unknownMap = unknownMap;
        this.noisyOracles = noisyOracles;
        EstimateOraclesTrust();
    }


    public void EstimateOraclesTrust(){
        // Positive training data
        for(NoisyOracle noisyOracle: noisyOracles){
            int etp = 0;
            for (String s : posMap.getMap().keySet()) {
                for (String t : posMap.getMap().get(s).keySet()) {
                    etp += (noisyOracle.predict(s, t)) ? 1 : 0 ;
                }
            }
            noisyOracle.estimatedTp = etp / posMap.size();
        }
        // Negative training data
        for(NoisyOracle noisyOracle: noisyOracles){
            int etn = 0;
            for (String s : negMap.getMap().keySet()) {
                for (String t : negMap.getMap().get(s).keySet()) {
                    etn += (!noisyOracle.predict(s, t)) ? 1 : 0 ;
                }
            }
            noisyOracle.estimatedTp = etn / negMap.size();
        }
    }

    public double estimateTp(AMapping map,NoisyOracle noisyOracle){
        double num =0.0d, denum =0.0d;
        for (String s : map.getMap().keySet()) {
            for (String t : map.getMap().get(s).keySet()) {
                if(posMap.contains(s, t)){
                    num += (noisyOracle.predict(s, t)) ? 1 : 0 ;
                    denum += 2 * posMap.getConfidence(s, t) -1;
                }
            }
        }
        return num/denum;
    }
    
    public double estimateTn(AMapping map,NoisyOracle noisyOracle){
        double num =0.0d, denum =0.0d;
        for (String s : map.getMap().keySet()) {
            for (String t : map.getMap().get(s).keySet()) {
                if(negMap.contains(s, t)){
                    num += (!noisyOracle.predict(s, t)) ? 1 : 0 ;
                    denum += 1 - 2 * posMap.getConfidence(s, t);
                }
            }
        }
        return num/denum;
    }


    public void updateTrainingData(AMapping nonlabeledMap){
        for (String s : nonlabeledMap.getMap().keySet()) {
            for (String t : nonlabeledMap.getMap().get(s).keySet()) {
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


    protected double estimateTrue(String subject, String object){
        double result = 1; 
        for(NoisyOracle noisyOracle: noisyOracles){
            if(noisyOracle.predict(subject, object)){
                result *= 1 - (noisyOracle.predict(subject, object)? noisyOracle.estimatedTp: (1 - noisyOracle.estimatedTp));
            }
        }
        return 1 - result;
    }


    protected double estimateFalse(String subject, String object){
        double result = 1; 
        for(NoisyOracle noisyOracle: noisyOracles){
            if(noisyOracle.predict(subject, object)){
                result *= 1 - (!noisyOracle.predict(subject, object)? noisyOracle.estimatedTn: (1 - noisyOracle.estimatedTn));
            }
        }
        return 1 - result;
    }


    public double getTAU() {
        return TAU;
    }



    public void setTAU(double tAU) {
        TAU = tAU;
    }



    public AMapping getPosMap() {
        return posMap;
    }



    public void setPosMap(AMapping posMap) {
        this.posMap = posMap;
    }



    public AMapping getNegMap() {
        return negMap;
    }



    public void setNegMap(AMapping negMap) {
        this.negMap = negMap;
    }



    public AMapping getUnknownMap() {
        return unknownMap;
    }



    public void setUnknownMap(AMapping unknownMap) {
        this.unknownMap = unknownMap;
    }



    public List<NoisyOracle> getNoisyOracles() {
        return noisyOracles;
    }



    public void setNoisyOracles(List<NoisyOracle> noisyOracles) {
        this.noisyOracles = noisyOracles;
    }

    public static void main(String args[]){

    }
}
