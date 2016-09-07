package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an abstract class for the <b>Pseudo Precision</b>, <b>Pseudo Recall</b> and <b>Pseudo F-Measure</b> classes.<br>
 * It extends the abstract class <b>PRF</b> and implements additional methods that sets, gets and checks some flags values required for pseudo-measures. 
 * to switch on/off the claculation mode of being symmetric in case of precision or retrieving
 * one-to-one mapping
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public abstract class APseudoPRF extends APRF {
    static Logger logger = LoggerFactory.getLogger(APseudoPRF.class);
    /**A flag specifies if the calculated precision is symmetric or not     */
    public boolean symmetricPrecision = true;
    /** A flag specifies if the calculated considers only one-to-one mapping     */
    boolean useOneToOneMapping = false;

    
    /** 
     * The Abstract method to be implemented for calculating the accuracy of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated accuracy
     */
    public abstract double calculate(AMapping predictions, GoldStandard goldStandard);

    /**
     * The method checks if one-to-one mapping is used
     * @return boolean - true if one-to-one mapping is used
     */
    public boolean isUse1To1Mapping() {
        return useOneToOneMapping;
    }
    /**
     * The method retrieves the flag value of one-to-one mapping
     * @return boolean - the value of the one-to-one flag
     */
    public boolean getUse1To1Mapping() {
        return useOneToOneMapping;
    }
    /**
     * The method sets the one-to-one mapping flag
     * @param use1To1Mapping The boolean value to be assigned to the flag
     * 
     */
    public void setUse1To1Mapping(boolean use1To1Mapping) {
        this.useOneToOneMapping = use1To1Mapping;
    }
    /**
     * The method checks if symmetric precision flag is used
     * @return boolean - true if symmetric precision is used
     */
    public boolean isSymmetricPrecision() {
        return symmetricPrecision;
    }
    /**
     * The method sets the symmetric precision flag
     * @param symmetricPrecision The boolean value to be assigned to the flag
     */
    public void setSymmetricPrecision(boolean symmetricPrecision) {
        this.symmetricPrecision = symmetricPrecision;
    }
}
