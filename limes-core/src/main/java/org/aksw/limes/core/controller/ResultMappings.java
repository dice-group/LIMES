package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

/**
 * @author sherif
 */
public class ResultMappings {
    protected AMapping verificationMapping;
    protected AMapping acceptanceMapping;

    /**
     * Constructor
     */
    ResultMappings() {
        this.verificationMapping = MappingFactory.createDefaultMapping();
        this.acceptanceMapping = MappingFactory.createDefaultMapping();
    }

    /**
     * Constructor
     * @param verificationMapping Mapping where acceptanceThreshold &gt; sim &gt;= verificationThreshold
     * @param acceptanceMapping Mapping where sim &gt;= acceptanceThreshold
     */
    public ResultMappings(AMapping verificationMapping, AMapping acceptanceMapping) {
        super();
        this.verificationMapping = verificationMapping;
        this.acceptanceMapping = acceptanceMapping;
    }

    /**
     * Getter for verification part
     * @return verification mapping
     */
    public AMapping getVerificationMapping() {
        return verificationMapping;
    }


    /**
     * Getter for acceptance part
     * @return acceptance mapping
     */
    public AMapping getAcceptanceMapping() {
        return acceptanceMapping;
    }

}
