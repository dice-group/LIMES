package org.aksw.limes.core.ml.algorithm.ligon;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Kevin Dreßler
 */
public interface Oracle {

    boolean predict(String subject, String object);

    AMapping getOracleMap();

    void setOracleMap(AMapping oracleMap);

}
