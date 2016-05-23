package org.aksw.limes.core.ml;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.junit.Test;

import static org.junit.Assert.*;

public class WombatUsageTest {

	@Test
	public void testSupervisedBatch() {

		SupervisedMLAlgorithm wombatSimple = null;
		try {
			wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class, 
					MLImplementationType.SUPERVISED_BATCH).asSupervised();
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
			fail();
		}
		assert (wombatSimple.getClass().equals(SupervisedMLAlgorithm.class));
		
	}

//	@Test
//	public void testUnsupervised() {
//
//		UnsupervisedMLAlgorithm wombatSimpleU = null;
//		try {
//			wombatSimpleU = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
//					MLImplementationType.UNSUPERVISED).asUnsupervised();
//		} catch (UnsupportedMLImplementationException e) {
//			e.printStackTrace();
//			fail();
//		}
//		assert (wombatSimpleU.getClass().equals(UnsupervisedMLAlgorithm.class));
//
//	}

}
