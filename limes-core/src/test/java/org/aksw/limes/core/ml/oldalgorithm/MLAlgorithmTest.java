package org.aksw.limes.core.ml.oldalgorithm;

import org.aksw.limes.core.ml.setting.ActiveLearningSetting;
import org.junit.Test;

/**
 * TODO Assertions!
 *
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 * @version 2015-11-09
 */
public class MLAlgorithmTest {

    @Test
    public void test() {

        EagleUnsupervised eagle;
        eagle = new EagleUnsupervised(null, null, null);
        try {
            System.out.println("Default termination criteria.");
            ActiveLearningSetting als = new ActiveLearningSetting(eagle);
            eagle.init(als, null);
            als.learn();

            System.out.println("Custom termination criteria.");
            MyActiveLearning mals = new MyActiveLearning(eagle);
            eagle.init(mals, null);
            mals.learn();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}

class MyActiveLearning extends ActiveLearningSetting {

    public MyActiveLearning(MLAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public boolean terminate() {

        int iter = this.getCurrentIteration();

        if (iter > 1)
            return true;

        return false;
    }

}