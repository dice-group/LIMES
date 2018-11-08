package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

/**
 * @author sherif
 */
public class ResultMappings {
    protected AMapping verificationMapping;
    protected AMapping acceptanceMapping;
    private LimesStatistics statistics;

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
     * Constructor
     * @param verificationMapping Mapping where acceptanceThreshold &gt; sim &gt;= verificationThreshold
     * @param acceptanceMapping Mapping where sim &gt;= acceptanceThreshold
     * @param statistics Statistics information
     */
    public ResultMappings(AMapping verificationMapping, AMapping acceptanceMapping, LimesStatistics statistics) {
        this(verificationMapping, acceptanceMapping);
        this.statistics = statistics;
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

    public LimesStatistics getStatistics() {
        return statistics;
    }

    public static class LimesStatistics {

        private int sourceSize, targetSize;
        private long mappingTime;
        private int acceptanceSize, verificationSize;

        public LimesStatistics(int sourceSize, int targetSize, long mappingTime, int verificationSize, int acceptanceSize) {
            this.sourceSize = sourceSize;
            this.targetSize = targetSize;
            this.mappingTime = mappingTime;
            this.acceptanceSize = acceptanceSize;
            this.verificationSize = verificationSize;
        }

        public long getMappingTime() {
            return mappingTime;
        }

        public int getAcceptanceSize() {
            return acceptanceSize;
        }

        public int getVerificationSize() {
            return verificationSize;
        }

        public int getTargetSize() {
            return targetSize;
        }

        public int getSourceSize() {
            return sourceSize;
        }

        public String toString() {
            return String.format("{\n\t\"inputSizes\" : {\n\t\t\"source\" : %d," +
                            "\n\t\t\"target\" : %d\n\t},\n\t\"mappingTime\" : %d,\n\t" +
                            "\"outputSizes\" : {\n\t\t\"verification\" : %d,\n\t\t" +
                            "\"acceptance\" : %d\n\t}\n}",
                    this.getSourceSize(), this.getTargetSize(), this.getMappingTime(),
                    this.getVerificationSize(), this.getAcceptanceSize());
        }
    }
}
