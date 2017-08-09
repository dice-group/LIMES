package org.aksw.limes.core.ml.algorithm.ligon_old;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class NoisyOracle {

    protected AMapping oracleMap;
    protected double tp, tn;
    protected double estimatedTp, estimatedTn;
    
    


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
            return (random.nextDouble() <= tp) ? true : false;
        }else{
            return (random.nextDouble() <= tn) ? false: true ;
        }
    }


    @Override
    public String toString() {
        return "NoisyOracle [oracleMap=" + oracleMap + ", tp=" + tp + ", tn="
                + tn + ", estimatedTp=" + estimatedTp + ", estimatedTn="
                + estimatedTn + "]";
    }

    public AMapping getOracleMap() {
        return oracleMap;
    }


    public void setOracleMap(AMapping oracleMap) {
        this.oracleMap = oracleMap;
    }


    public double getTp() {
        return tp;
    }


    public void setTp(double tp) {
        this.tp = tp;
    }


    public double getTn() {
        return tn;
    }


    public void setTn(double tn) {
        this.tn = tn;
    }


    public double getEstimatedTp() {
        return estimatedTp;
    }


    public void setEstimatedTp(double estimatedTp) {
        this.estimatedTp = estimatedTp;
    }


    public double getEstimatedTn() {
        return estimatedTn;
    }


    public void setEstimatedTn(double estimatedTn) {
        this.estimatedTn = estimatedTn;
    }

}
