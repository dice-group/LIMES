package org.aksw.limes.core.ml;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.fail;

public class WombatCompleteTest {

    Cache sc = new MemoryCache();
    Cache tc = new MemoryCache();

    AMapping trainingMap, refMap;

    @Before
    public void init() {
        List<String> props = new LinkedList<String>();
        props.add("name");
        props.add("surname");

        Instance i1 = new Instance("ex:i1");
        i1.addProperty("name", "Klaus");
        i1.addProperty("surname", "Lyko");
        Instance i2 = new Instance("ex:i2");
        i2.addProperty("name", "John");
        i2.addProperty("surname", "Doe");
        Instance i3 = new Instance("ex:i3");
        i3.addProperty("name", "Claus");
        i3.addProperty("surname", "Stadler");

        sc.addInstance(i1);
        sc.addInstance(i3);

        tc.addInstance(i1);
        tc.addInstance(i2);
        tc.addInstance(i3);

        PropertyMapping pm = new PropertyMapping();
        pm.addStringPropertyMatch("name", "name");
        pm.addStringPropertyMatch("surname", "surname");

        trainingMap = MappingFactory.createDefaultMapping();
        trainingMap.add("ex:i1", "ex:i1", 1d);

        refMap = MappingFactory.createDefaultMapping();
        refMap.add("ex:i1", "ex:i1", 1d);
        refMap.add("ex:i3", "ex:i3", 1d);
    }

    @Test
    public void testSupervisedBatch() throws UnsupportedMLImplementationException {
        SupervisedMLAlgorithm wombatComplete = null;
        try {
            wombatComplete = MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,
                    MLImplementationType.SUPERVISED_BATCH).asSupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (wombatComplete.getClass().equals(SupervisedMLAlgorithm.class));
        wombatComplete.init(null, sc, tc);
        MLModel mlModel = wombatComplete.learn(trainingMap);
        AMapping resultMap = wombatComplete.predict(sc, tc, mlModel);
        assert (resultMap.equals(refMap));
    }


    @Test
    public void testUnsupervised() throws UnsupportedMLImplementationException {
        UnsupervisedMLAlgorithm wombatCompleteU = null;
        try {
            wombatCompleteU = MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,
                    MLImplementationType.UNSUPERVISED).asUnsupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (wombatCompleteU.getClass().equals(UnsupervisedMLAlgorithm.class));
        trainingMap = null;
        wombatCompleteU.init(null, sc, tc);
        MLModel mlModel = wombatCompleteU.learn(new PseudoFMeasure());
        AMapping resultMap = wombatCompleteU.predict(sc, tc, mlModel);
        assert (resultMap.equals(refMap));
    }

}
