package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.junit.Test;

public class EagleUnsupervisedTest {
	@Test
	public void test() {
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
		
		Configuration config = new Configuration();
		KBInfo si = new KBInfo();
		si.setVar("?x");
		si.setProperties(props);
		
		KBInfo ti = new KBInfo();
		ti.setVar("?y");
		ti.setProperties(props);
		
		config.setSourceInfo(si);
		config.setTargetInfo(ti);
		
		PropertyMapping pm = new PropertyMapping();
		pm.addStringPropertyMatch("name", "name");
		pm.addStringPropertyMatch("surname", "surname");
		
		EagleUnsupervised eus = new EagleUnsupervised(sc, tc, config);
		
		UnsupervisedLearningSetting param = new UnsupervisedLearningSetting(eus);
		param.setPropMap(pm);
		
		try {
			eus.init(param, null);
			
			MLResult result = eus.learn(null);
			System.out.println(result);
			System.out.println(result.getMapping());
			
			assertTrue(result != null);
			assertTrue(result.mapping.size()>0);
			assertTrue(!result.linkspec.isEmpty());
			assertTrue(result.details.size()>0);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
