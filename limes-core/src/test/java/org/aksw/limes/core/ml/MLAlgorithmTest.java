package org.aksw.limes.core.ml;
import org.aksw.limes.core.ml.algorithm.EagleUnsupervised;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;
import org.aksw.limes.core.ml.setting.ActiveLearningSetting;
import org.junit.Test;

/**
 * TODO Assertions!
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-09
 *
 */
public class MLAlgorithmTest {

	@Test
	public void test() {
		
		EagleUnsupervised eagle = new EagleUnsupervised(null, null, null);

		System.out.println("Default termination criteria.");
		ActiveLearningSetting als = new ActiveLearningSetting(eagle);
		als.learn();
		
		System.out.println("Custom termination criteria.");
		MyActiveLearning mals = new MyActiveLearning(eagle);
		mals.learn();
		
	}
	

}

class MyActiveLearning extends ActiveLearningSetting {

	public MyActiveLearning(MLAlgorithm algorithm) {
		super(algorithm);
	}
	
	@Override
	public boolean terminate() {
		
		int iter = this.getCurrentIteration();
		
		if(iter > 1)
			return true;
		
		return false;
	}
	
}