package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.algorithm.ligon_old.Ligon;
import org.aksw.limes.core.ml.algorithm.ligon_old.NoisyOracle;
import org.junit.Before;
import org.junit.Test;

public class LigonOldTest {

	ACache sc = new MemoryCache();
	ACache tc = new MemoryCache();

	AMapping posTrainingMap, negTrainingMap, unkownTrainingMap, refMap;

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

		Instance i4 = new Instance("ex:i4");
		i4.addProperty("name", "Mohamed");
		i4.addProperty("surname", "Sherif");

		Instance i5 = new Instance("ex:i5");
		i5.addProperty("name", "tommso");
		i5.addProperty("surname", "Soru");

		Instance i6 = new Instance("ex:i6");
		i6.addProperty("name", "Kevin");
		i6.addProperty("surname", "Dressler");

		sc.addInstance(i1);
		sc.addInstance(i3);
		sc.addInstance(i4);
		sc.addInstance(i5);
		sc.addInstance(i6);

		tc.addInstance(i1);
		tc.addInstance(i2);
		tc.addInstance(i3);
		tc.addInstance(i5);
		tc.addInstance(i6);

		PropertyMapping pm = new PropertyMapping();
		pm.addStringPropertyMatch("name", "name");
		pm.addStringPropertyMatch("surname", "surname");

		posTrainingMap = MappingFactory.createDefaultMapping();
		posTrainingMap.add("ex:i1", "ex:i1", 1d);

		negTrainingMap = MappingFactory.createDefaultMapping();
		negTrainingMap.add("ex:i4", "ex:i3", 1d);

		unkownTrainingMap = MappingFactory.createDefaultMapping();
		unkownTrainingMap.add("ex:i6", "ex:i6", 1d);
		unkownTrainingMap.add("ex:i6", "ex:i6", 1d);

		refMap = MappingFactory.createDefaultMapping();
		refMap.add("ex:i1", "ex:i1", 1d);
		refMap.add("ex:i3", "ex:i3", 1d);
	}

	@Test
	public void testLigon()
			throws UnsupportedMLImplementationException {
		SupervisedMLAlgorithm fuzzyWombat = null;
		try {
			fuzzyWombat = MLAlgorithmFactory
					.createMLAlgorithm(FuzzyWombatSimple.class,
							MLImplementationType.SUPERVISED_BATCH)
					.asSupervised();
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
			fail();
		}
		assert (fuzzyWombat.getClass().equals(SupervisedMLAlgorithm.class));
		fuzzyWombat.init(null, sc, tc);
		MLResults mlModel = fuzzyWombat.learn(posTrainingMap);
		AMapping resultMap = fuzzyWombat.predict(sc, tc, mlModel);
		//        assert (resultMap.equals(refMap));

		NoisyOracle no1a = new NoisyOracle(refMap, 1.0, 1.0);
		NoisyOracle no1b = new NoisyOracle(refMap, 1.0, 1.0);
		NoisyOracle no1c = new NoisyOracle(refMap, 1.0, 1.0);
		NoisyOracle no2a = new NoisyOracle(refMap, 0.5, 0.5);
		NoisyOracle no2b = new NoisyOracle(refMap, 0.5, 0.5);
		NoisyOracle no2c = new NoisyOracle(refMap, 0.5, 0.5);
		NoisyOracle no3a = new NoisyOracle(refMap, 0.0, 0.0);
		NoisyOracle no3b = new NoisyOracle(refMap, 0.0, 0.0);
		NoisyOracle no3c = new NoisyOracle(refMap, 0.0, 0.0);
		List<NoisyOracle> noisyOracles = new ArrayList<>(Arrays.asList(no1a, no2a, no3a, no1b, no2b, no3b, no1c, no2c, no3c));

		Ligon l = new Ligon(posTrainingMap, sc, tc, noisyOracles);
		System.out.println(l.getNoisyOracles());

	}



}