package org.aksw.limes.core.ml;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.algorithm.ACIDS;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.EAGLE;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.UnsupervisedMLAlgorithm;
import org.junit.Test;

import static org.junit.Assert.*;

public class UsageTest {

	@Test
	public void testSupervisedBatch() {

		SupervisedMLAlgorithm eagle = null;
		try {
			eagle = new SupervisedMLAlgorithm(EAGLE.class);
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
			fail();
		}
		assert (eagle.getClass().equals(SupervisedMLAlgorithm.class));
		
		
		
	}

	@Test
	public void testUnsupervised() {

		UnsupervisedMLAlgorithm eagleU = null;
		try {
			eagleU = new UnsupervisedMLAlgorithm(EAGLE.class);
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
			fail();
		}
		assert (eagleU.getClass().equals(UnsupervisedMLAlgorithm.class));

	}

	@Test
	public void testActive() {

		ActiveMLAlgorithm acids = null;
		try {
			acids = new ActiveMLAlgorithm(ACIDS.class);
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
			fail();
		}
		assert (acids.getClass().equals(ActiveMLAlgorithm.class));

	}

	@Test
	public void testFailure() {

		boolean itFails = false;
		try {
			new ActiveMLAlgorithm(EAGLE.class);
		} catch (UnsupportedMLImplementationException e) {
			itFails = true;
		}
		assert (itFails);

	}

}
