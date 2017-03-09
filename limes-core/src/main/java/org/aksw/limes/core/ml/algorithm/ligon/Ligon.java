package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ligon {
    static Logger logger = LoggerFactory.getLogger(Ligon.class);
    
    List<NoisyOracle> noisyOracles;
    List<Double> oraclesTrust;
    
    Ligon(){
        noisyOracles = new ArrayList<>();
        oraclesTrust = new ArrayList<>();
    }
    
    boolean predict(String subject, String object){
        double unionProp = noisyOracles.get(0).predict(subject, object)? 1.0d : 0.0d ;
        for(int i = 1 ; i<noisyOracles.size() ; i++){
            double oraclePredict = noisyOracles.get(i).predict(subject, object)? 1.0d : 0.0d ;
            unionProp += oraclePredict - unionProp * oraclePredict; 
        }
        return (unionProp == 1) ? true : false;
    }
    
    void updateOracles(){
        
    }
    
    
    public static void main(String args[]){
        
    }
}
