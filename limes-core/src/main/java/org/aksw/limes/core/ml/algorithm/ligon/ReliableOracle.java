package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Random;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class ReliableOracle {

    protected AMapping oracleMap;


    public ReliableOracle(AMapping oracleMap) {
        super();
        this.oracleMap = oracleMap;
    }

    boolean predict(String subject, String object){
        return oracleMap.contains(subject, object);
    }

    public AMapping getOracleMap() {
        return oracleMap;
    }

    public void setOracleMap(AMapping oracleMap) {
        this.oracleMap = oracleMap;
    }

}
