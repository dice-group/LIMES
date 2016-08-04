package org.aksw.limes.core.ml;

import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.junit.Before;
import org.junit.Test;

//Temporary version
//TODO finish
public class DecisionTreeLearningTest {

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
    public void testActive() throws UnsupportedMLImplementationException {
        ActiveMLAlgorithm dtl = null;
        try {
            dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (dtl.getClass().equals(ActiveMLAlgorithm.class));
        dtl.init(null, sc, tc);
        dtl.activeLearn();
        AMapping nextExamples = dtl.getNextExamples(3);
        AMapping oracleFeedback = oracleFeedback(nextExamples,trainingMap);
        MLResults mlModel = dtl.activeLearn(oracleFeedback);
        AMapping resultMap = dtl.predict(sc, tc, mlModel);
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
