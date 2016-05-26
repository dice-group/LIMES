package org.aksw.limes.core.ml;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.algorithm.*;
import org.junit.Test;

import static org.junit.Assert.fail;

public class EagleUsageTest {

    @Test
    public void testSupervisedBatch() {

        SupervisedMLAlgorithm eagle = null;
        try {
            eagle = MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
                    MLImplementationType.SUPERVISED_BATCH).asSupervised();
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
            eagleU = MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
                    MLImplementationType.UNSUPERVISED).asUnsupervised();
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
            acids = MLAlgorithmFactory.createMLAlgorithm(ACIDS.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
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
            MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
                    MLImplementationType.SUPERVISED_ACTIVE);
        } catch (UnsupportedMLImplementationException e) {
            itFails = true;
        }
        assert (itFails);

    }

}
