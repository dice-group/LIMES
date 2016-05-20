package org.aksw.limes.core.ml;

import org.aksw.limes.core.ml.algorithmtest.EAGLE;
import org.aksw.limes.core.ml.algorithmtest.SupervisedMLAlgorithm;

public class UsageTest {

	public static void main(String[] args) {
		
		SupervisedMLAlgorithm eagle = null;
		try {
			eagle = new SupervisedMLAlgorithm(EAGLE.class);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

}
