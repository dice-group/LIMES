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
     */
    public ResultMappings(AMapping verificationMapping, AMapping acceptanceMapping) {
        super();
        this.verificationMapping = verificationMapping;
        this.acceptanceMapping = acceptanceMapping;
    }

    /**
     * @return the verification mapping
     */
    public AMapping getVerificationMapping() {
        return verificationMapping;
    }

    /**
     * @param verificationMapping
     */
    public void setVerificationMapping(AMapping verificationMapping) {
        this.verificationMapping = verificationMapping;
    }

    /**
     * @return acceptance mapping
     */
    public AMapping getAcceptanceMapping() {
        return acceptanceMapping;
    }

    /**
     * @param acceptanceMapping
     */
    public void setAcceptanceMapping(AMapping acceptanceMapping) {
        this.acceptanceMapping = acceptanceMapping;
    }


}
