package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.*;

import java.io.IOException;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.aksw.limes.core.measures.mapper.space.Flink.FlinkHR3Mapper;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.junit.Before;
import org.junit.Test;


public class HR3FlinkTest {
	
    public ACache source;
    public ACache target;
    
    public DataSet<Instance> sourceDS;
    public DataSet<Instance> targetDS;
	final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    @Before
    public void setUp() throws IOException {
        source = new MemoryCache();
        target = new MemoryCache();

        Instance s1 = new Instance("S1");
        s1.addProperty("age", "26");
        s1.addProperty("derp", "260");
        s1.addProperty("derp", "20");
        s1.addProperty("florp", "200000");

        Instance s2 = new Instance("S2");
        s2.addProperty("age", "6");
        s2.addProperty("derp", "60");
        s2.addProperty("florp", "20000");

        Instance s3 = new Instance("S3");
        s3.addProperty("age", "28");
        s3.addProperty("derp", "49");
        s3.addProperty("florp", "20300");

        Instance s4 = new Instance("S4");
        s4.addProperty("age", "3");
        s4.addProperty("derp", "72");
        s4.addProperty("florp", "300");

        Instance t1 = new Instance("T1");
        t1.addProperty("age", "26");
        t1.addProperty("derp", "260");
        t1.addProperty("derp", "20");
        t1.addProperty("florp", "200000");

        Instance t2 = new Instance("T2");
        t2.addProperty("age", "6");
        t2.addProperty("derp", "60");
        t2.addProperty("florp", "200000");

        Instance t3 = new Instance("T3");
        t3.addProperty("age", "26");
        t3.addProperty("derp", "49");
        t3.addProperty("derp", "20");
        t3.addProperty("florp", "200000");

        Instance t4 = new Instance("T4");
        t4.addProperty("age", "3");
        t4.addProperty("derp", "72");
        t4.addProperty("florp", "200000");
        
        source.addInstance(s1);
        source.addInstance(s2);
        source.addInstance(s3);
        source.addInstance(s4);

        target.addInstance(t1);
        target.addInstance(t2);
        target.addInstance(t3);
        target.addInstance(t4);
        
        sourceDS = env.fromElements(s1, s2, s3, s4);
        targetDS = env.fromElements(t1, t2, t3, t4);

        sourceDS = cacheToDS(source); 
        targetDS = cacheToDS(target);
    }
    
    private DataSet<Instance> cacheToDS(ACache c){
    	return env.fromCollection(c.getAllInstances());
    }

    @Test
    public void testi() throws Exception {
    	String measureExpr = "euclidean(x.derp|age|florp, y.derp|age|florp)";
    	HR3Mapper hr3m = new HR3Mapper();
    	AMapping regM = hr3m.getMapping(source, target, "?x", "?y", measureExpr, 0.9);


    	FlinkHR3Mapper flinkhr3m = new FlinkHR3Mapper();
    	AMapping flinkM = flinkhr3m.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, 0.9);
    	assertEquals(regM,flinkM);
    }

}
