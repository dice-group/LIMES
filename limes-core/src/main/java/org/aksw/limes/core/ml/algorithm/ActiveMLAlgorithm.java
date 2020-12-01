package org.aksw.limes.core.ml.algorithm;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public class ActiveMLAlgorithm extends AMLAlgorithm {

	public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;

	/**
	 * @param clazz
	 *            the CoreMLAlgorithm class
	 * @throws UnsupportedMLImplementationException
	 *             if ML implementation is not supported
	 */
	public ActiveMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

		try {
			final Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
			this.setMl(ctor.newInstance());
		} catch (final Exception e) {
			throw new UnsupportedMLImplementationException(this.getMl().getName());
		}

		if (!this.getMl().supports(ML_IMPLEMENTATION_TYPE)) {
			throw new UnsupportedMLImplementationException(this.getMl().getName());
		}

	}

	/**
	 * @param size
	 *            number of examples to return
	 * @return the mapping
	 * @throws UnsupportedMLImplementationException
	 *             if ML implementation is not supported
	 */
	public AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		return this.getMl().getNextExamples(size);
	}

	/**
	 * @return wrap with results
	 * @throws UnsupportedMLImplementationException
	 *             Exception
	 */
	public MLResults activeLearn() throws UnsupportedMLImplementationException {
		return this.getMl().activeLearn();
	}

	/**
	 * @param oracleMapping
	 *            mapping from the oracle
	 * @return wrap with results
	 * @throws UnsupportedMLImplementationException
	 *             if ML implementation is not supported
	 */
	public MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
		return this.getMl().activeLearn(oracleMapping);
	}

}
