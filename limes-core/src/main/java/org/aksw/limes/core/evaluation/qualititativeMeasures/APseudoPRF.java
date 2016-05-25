package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * * This class cointains three methods that calculate such values:
 * This class is an extension of PRF and it combines common flags among the pesudo-measures
 * to switch on/off the claculation mode of being symmetric in case of precision or retrieving
 * one-to-one mapping
 * @author mofeed
 * @version 1.0
 */
public abstract class APseudoPRF extends APRF{
    public abstract double calculate(Mapping predictions, GoldStandard goldStandard);

    public boolean symmetricPrecision = true;
    boolean useOneToOneMapping = false;
    /**
     * @return the useOneToOneMapping
     */
    public boolean isUse1To1Mapping() {
        return useOneToOneMapping;
    }
    public void setUse1To1Mapping(boolean use1To1Mapping) {
        this.useOneToOneMapping = use1To1Mapping;
    }
    public boolean getUse1To1Mapping() {
        return useOneToOneMapping;
    }
    public boolean isSymmetricPrecision() {
        return symmetricPrecision;
    }
    public void setSymmetricPrecision(boolean symmetricPrecision) {
        this.symmetricPrecision = symmetricPrecision;
    }
}
