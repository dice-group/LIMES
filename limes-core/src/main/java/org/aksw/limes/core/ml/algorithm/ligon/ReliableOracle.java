package org.aksw.limes.core.ml.algorithm.ligon;

import org.aksw.limes.core.io.mapping.AMapping;

public class ReliableOracle implements Oracle {

    protected AMapping oracleMap;


    public ReliableOracle(AMapping oracleMap) {
        super();
        this.oracleMap = oracleMap;
    }

    public boolean predict(String subject, String object) {
        return oracleMap.contains(subject, object);
    }

    public AMapping getOracleMap() {
        return oracleMap;
    }

    public void setOracleMap(AMapping oracleMap) {
        this.oracleMap = oracleMap;
    }

}
