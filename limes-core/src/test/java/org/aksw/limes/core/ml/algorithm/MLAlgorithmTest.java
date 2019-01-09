package org.aksw.limes.core.ml.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * Basic test class for ml algorithms. Provides an ad hoc test case.
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public class MLAlgorithmTest {
	static Logger logger = Logger.getLogger(MLAlgorithmTest.class);
    ACache sc = new MemoryCache();
    ACache tc = new MemoryCache();

    ACache extendedSourceCache = new MemoryCache();
    ACache extendedTargetCache = new MemoryCache();
    
    AMapping trainingMap, extendedTrainingMap, refMap;
    
    Configuration config = new Configuration();
    PropertyMapping pm = new PropertyMapping();

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
        i4.addProperty("name", "Claus");
        i4.addProperty("surname", "Lyko");        
        Instance i5 = new Instance("ex:i5");
        i5.addProperty("name", "J.");
        i5.addProperty("surname", "Doe");
        
        Instance i6 = new Instance("ex:i6");
        i6.addProperty("name", "Donald");
        i6.addProperty("surname", "Dumb");


        sc.addInstance(i1);//lyko
        sc.addInstance(i2);//j.doe
        sc.addInstance(i3);//stadler
        sc.addInstance(i6);//trump
        

        tc.addInstance(i1);
        tc.addInstance(i2);
        tc.addInstance(i3);
        tc.addInstance(i4);
        tc.addInstance(i5);
        tc.addInstance(i6);
        
        
        //extra Instances
        Instance ia = new Instance("ex:ia");
        ia.addProperty("name", "Phill");
        ia.addProperty("surname", "Zlaus");
        Instance ib = new Instance("ex:ib");
        ib.addProperty("name", "Al");
        ib.addProperty("surname", "Lektro");

        for(Instance ii : sc.getAllInstances())
        	extendedSourceCache.addInstance(ii);
        for(Instance ii : tc.getAllInstances())
        	extendedTargetCache.addInstance(ii);
        extendedSourceCache.addInstance(ia);
        extendedTargetCache.addInstance(ia);
        
        trainingMap = MappingFactory.createDefaultMapping();
        trainingMap.add("ex:i1", "ex:i1", 1d);

        refMap = MappingFactory.createDefaultMapping();
        refMap.add("ex:i1", "ex:i1", 1d);
        refMap.add("ex:i3", "ex:i3", 1d);
        refMap.add("ex:i6", "ex:i6", 1d);

        extendedTrainingMap = MappingFactory.createDefaultMapping();
        for(Entry<String, HashMap<String, Double>> e : refMap.getMap().entrySet()) {
        	for(String t : e.getValue().keySet())
        	extendedTrainingMap.add(e.getKey(), t, refMap.getConfidence(e.getKey(), t));
        }
        extendedTrainingMap.add("ex:i1", "ex:i4", 0.8d);
        extendedTrainingMap.add("ex:i2", "ex:i5", 1d);
        
        KBInfo si = new KBInfo();
        si.setVar("?x");
        si.setProperties(props);

        KBInfo ti = new KBInfo();
        ti.setVar("?y");
        ti.setProperties(props);

        config.setSourceInfo(si);
        config.setTargetInfo(ti);

        pm.addStringPropertyMatch("name", "name");
        pm.addStringPropertyMatch("surname", "surname");

    }

}
