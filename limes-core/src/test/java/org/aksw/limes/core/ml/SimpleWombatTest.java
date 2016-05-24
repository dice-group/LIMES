package org.aksw.limes.core.ml;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.oldalgorithm.Lion;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

public class SimpleWombatTest {

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
		
	    Cache sc = new MemoryCache();
        Cache tc = new MemoryCache();
        
        List<String> props = new LinkedList<String>();
        props.add("name");
        props.add("surname");
        
        Instance i1 = new Instance("ex:i1");
        i1.addProperty("name", "Klaus");
        i1.addProperty("surname", "Lyko");
        Instance i2 = new Instance("ex:i2");
        i2.addProperty("name", "John");
        i2.addProperty("surname", "Doe");
        Instance i3= new Instance("ex:i3");
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
        
        Mapping trainingData = MappingFactory.createDefaultMapping();
        trainingData.add("ex:i1", "ex:i1", 1d);
        wombatSimple.init(null, sc, tc);
        MLModel mlModel = wombatSimple.learn(trainingData);
        Mapping resultMap = wombatSimple.predict(sc, tc, mlModel);
        
        Mapping refMap = MappingFactory.createDefaultMapping();
        refMap.add("ex:i1", "ex:i1", 1d);
        refMap.add("ex:i3", "ex:i3", 1d);
        System.out.println("Result: " + refMap);
        
        assert(resultMap.equals(refMap));
        
        
//        Lion lion = new Lion(sc, tc, config);
//        
//        UnsupervisedLearningSetting param = new UnsupervisedLearningSetting(lion);
//        param.setPropMap(pm);
//        param.setMaxDuration(5);
//        try {
//            lion.init(param, null);
//            
//            MLModel result = lion.learn(null);
//            System.out.println(result);
//            System.out.println(result.getMapping());
//            
//            assertTrue(result != null);
//            assertTrue(result.mapping.size()>0);
//            assertTrue(!result.linkspec.isEmpty());
//        
//            
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
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
