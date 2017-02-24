package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.UnsupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.junit.Before;
import org.junit.Test;

public class WombatSimpleTest {

    ACache sc = new MemoryCache();
    ACache tc = new MemoryCache();

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
        SupervisedMLAlgorithm wombatSimple = null;
        try {
            wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                    MLImplementationType.SUPERVISED_BATCH).asSupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (wombatSimple.getClass().equals(SupervisedMLAlgorithm.class));
        wombatSimple.init(null, sc, tc);
        MLResults mlModel = wombatSimple.learn(trainingMap);
        AMapping resultMap = wombatSimple.predict(sc, tc, mlModel);
        assert (resultMap.equals(refMap));  
    }


    @Test
    public void testUnsupervised() throws UnsupportedMLImplementationException {
        UnsupervisedMLAlgorithm wombatSimpleU = null;
        try {
            wombatSimpleU = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                    MLImplementationType.UNSUPERVISED).asUnsupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (wombatSimpleU.getClass().equals(UnsupervisedMLAlgorithm.class));
        trainingMap = null;
        wombatSimpleU.init(null, sc, tc);
        MLResults mlModel = wombatSimpleU.learn(new PseudoFMeasure());
        AMapping resultMap = wombatSimpleU.predict(sc, tc, mlModel);
        assert (resultMap.equals(refMap));
    }
    
    @Test
    public void testActive() throws UnsupportedMLImplementationException {
        ActiveMLAlgorithm wombatSimpleA = null;
        try {
            wombatSimpleA = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (wombatSimpleA.getClass().equals(ActiveMLAlgorithm.class));
        wombatSimpleA.init(null, sc, tc);
        wombatSimpleA.activeLearn();
        AMapping nextExamples = wombatSimpleA.getNextExamples(3);
        AMapping oracleFeedback = oracleFeedback(nextExamples,trainingMap);
        MLResults mlModel = wombatSimpleA.activeLearn(oracleFeedback);
        AMapping resultMap = wombatSimpleA.predict(sc, tc, mlModel);
        assert (resultMap.equals(refMap));
    }
    
    private AMapping oracleFeedback(AMapping predictionMapping, AMapping referenceMapping) {
        AMapping result = MappingFactory.createDefaultMapping();

        for(String s : predictionMapping.getMap().keySet()){
            for(String t : predictionMapping.getMap().get(s).keySet()){
                if(referenceMapping.contains(s, t)){
                    result.add(s, t, predictionMapping.getMap().get(s).get(t));
                }
            }
        }
        return result;
    }

}
