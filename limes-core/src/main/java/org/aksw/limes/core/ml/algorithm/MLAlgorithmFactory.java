package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.apache.log4j.Logger;

public class MLAlgorithmFactory {

    public static final String EAGLE = "eagle";
    public static final String WOMBAT_SIMPLE = "wombat simple";
    public static final String WOMBAT_COMPLETE = "wombat complete";
    public static final String LION = "lion";

    public static final String SUPERVISED_ACTIVE = "supervised active";
    public static final String SUPERVISED_BATCH = "supervised batch";
    public static final String UNSUPERVISED = "unsupervised";

    public static final Logger logger = Logger.getLogger(MLAlgorithmFactory.class);

    public static Class<? extends ACoreMLAlgorithm> getAlgorithmType (String name) {
        if (name.equalsIgnoreCase(EAGLE)) {
            return Eagle.class;
        }
        if (name.equalsIgnoreCase(WOMBAT_SIMPLE)) {
            return WombatSimple.class;
        }
        if (name.equalsIgnoreCase(WOMBAT_COMPLETE)) {
            //@todo: fix this
            return null;
        }
        if (name.equalsIgnoreCase(LION)) {
            //@todo: fix this
            return null;
        }
        logger.warn(name + " is not implemented yet. Using 'wombat simple'...");
        return WombatSimple.class;
    }

    public static MLImplementationType getImplementationType (String name) {
        if (name.equalsIgnoreCase(SUPERVISED_ACTIVE)) {
            return MLImplementationType.SUPERVISED_ACTIVE;
        }
        if (name.equalsIgnoreCase(SUPERVISED_BATCH)) {
            return MLImplementationType.SUPERVISED_BATCH;
        }
        if (name.equalsIgnoreCase(UNSUPERVISED)) {
            return MLImplementationType.UNSUPERVISED;
        }
        logger.warn(name + " is not implemented yet. Using 'supervised batch' as default...");
        return MLImplementationType.SUPERVISED_BATCH;
    }

	public static AMLAlgorithm createMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz, MLImplementationType mlType) throws UnsupportedMLImplementationException {
		
		switch(mlType) {
		case SUPERVISED_BATCH:
			return new SupervisedMLAlgorithm(clazz);
		case UNSUPERVISED:
			return new UnsupervisedMLAlgorithm(clazz);
		case SUPERVISED_ACTIVE:
			return new ActiveMLAlgorithm(clazz);
		default:
			throw new UnsupportedMLImplementationException(clazz.getName());
		}

	}
	
}
