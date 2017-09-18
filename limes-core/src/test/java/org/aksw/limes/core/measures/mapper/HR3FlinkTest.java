package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.aksw.limes.core.measures.mapper.space.Flink.FlinkH3Mapper;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.junit.Before;
import org.junit.Test;

public class HR3FlinkTest {
	
//	public static HashSet<String> HR3sourceToCompare = new HashSet<>();
//	public static HashSet<String> FlinkHR3sourceToCompare = new HashSet<>();
//	public static HashSet<String> HR3Comparisons = new HashSet<>();
//	public static HashSet<String> FlinkHR3Comparisons = new HashSet<>();
	
    public ACache source;
    public ACache target;
    
    public DataSet<Instance> sourceDS;
    public DataSet<Instance> targetDS;
	final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    @Before
    public void setUp() throws IOException {
//        source = new MemoryCache();
//        target = new MemoryCache();
//
//        Instance s1 = new Instance("S1");
//        s1.addProperty("age", "26");
//        s1.addProperty("derp", "260");
//        s1.addProperty("derp", "20");
//        s1.addProperty("florp", "200000");
//
//        Instance s2 = new Instance("S2");
//        s2.addProperty("age", "6");
//        s2.addProperty("derp", "60");
//        s2.addProperty("florp", "20000");
//
//        Instance s3 = new Instance("S3");
//        s3.addProperty("age", "28");
//        s3.addProperty("derp", "49");
//        s3.addProperty("florp", "20300");
//
//        Instance s4 = new Instance("S4");
//        s4.addProperty("age", "3");
//        s4.addProperty("derp", "72");
//        s4.addProperty("florp", "300");
//
//        Instance t1 = new Instance("T1");
//        t1.addProperty("age", "26");
//        t1.addProperty("derp", "260");
//        t1.addProperty("derp", "20");
//        t1.addProperty("florp", "200000");
//
//        Instance t2 = new Instance("T2");
//        t2.addProperty("age", "6");
//        t2.addProperty("derp", "60");
//        t2.addProperty("florp", "200000");
//
//        Instance t3 = new Instance("T3");
//        t3.addProperty("age", "26");
//        t3.addProperty("derp", "49");
//        t3.addProperty("derp", "20");
//        t3.addProperty("florp", "200000");
//
//        Instance t4 = new Instance("T4");
//        t4.addProperty("age", "3");
//        t4.addProperty("derp", "72");
//        t4.addProperty("florp", "200000");
//        
//        source.addInstance(s1);
//        source.addInstance(s2);
//        source.addInstance(s3);
//        source.addInstance(s4);
//
//        target.addInstance(t1);
//        target.addInstance(t2);
//        target.addInstance(t3);
//        target.addInstance(t4);
//        
//        sourceDS = env.fromElements(s1, s2, s3, s4);
//        targetDS = env.fromElements(t1, t2, t3, t4);

//    	RDFConfigurationReader rdfcr = new RDFConfigurationReader("/home/ohdorno/gitrepo/swp15-ld-docs/Benchmarks/lgd-dbp-geo_long_lat.ttl");
//    	Configuration c = rdfcr.read();
//        ACache s = HybridCache.getData(c.getSourceInfo()).getSample(3000);
//        ACache t = HybridCache.getData(c.getTargetInfo()).getSample(1000);
//        source = new HybridCache();
//        target = new HybridCache();
//        for(Instance i : s.getAllInstances()){
//        	source.addInstance(i);
//        }
//        for(Instance i : s.getAllInstances()){
//        	target.addInstance(i);
//        }
//        ((HybridCache)source).saveToFile(new File("/home/ohdorno/git/LIMES-dev2/limes-core/cache/-1605365096.ser"));
//        ((HybridCache)source).saveToFile(new File("/home/ohdorno/git/LIMES-dev2/limes-core/cache/1412794763.ser"));
    	source = HybridCache.loadFromFile(new File("/home/ohdorno/git/LIMES-dev2/limes-core/cache/-1605365096.ser"));
    	target = HybridCache.loadFromFile(new File("/home/ohdorno/git/LIMES-dev2/limes-core/cache/1412794763.ser"));
        sourceDS = cacheToDS(source); 
        targetDS = cacheToDS(target);
        Files.write(Paths.get("/tmp/Flink"),"".getBytes());
        Files.write(Paths.get("/tmp/Reg"),"".getBytes());
    }
    
    private DataSet<Instance> cacheToDS(ACache c){
    	return env.fromCollection(c.getAllInstances());
    }

    @Test
    public void testi() throws Exception {
//    	String measureExpr = "euclidean(x.derp|age|florp, y.derp|age|florp)";
    	String measureExpr = "euclidean(x.geo:lat|geo:long, y.geo:lat|geo:long)";
    	HR3Mapper hr3m = new HR3Mapper();
    	long regStart = System.currentTimeMillis();
    	AMapping regM = hr3m.getMapping(source, target, "?x", "?y", measureExpr, 0.9);
    	long regStop = System.currentTimeMillis();


    	FlinkH3Mapper flinkhr3m = new FlinkH3Mapper();
    	long flinkStart = System.currentTimeMillis();
    	AMapping flinkM = flinkhr3m.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, 0.9);
    	long flinkStop = System.currentTimeMillis();
    	System.out.println("Reg comps: " + HR3Mapper.comparisons);
    	System.out.println("Flink comps: " + FlinkH3Mapper.comparisons);
//    	
    	System.out.println(regM.size());
    	System.out.println("=====");
    	System.out.println(flinkM.size());
    	System.out.println("RegTime: " + (regStop - regStart) + " RegStart: " + regStart + " Stop: " + regStop);
    	System.out.println("flinkTime: " + (flinkStop - flinkStart) + " flinkStart: " + flinkStart + " Stop: " + flinkStop);
    	assertEquals(regM,flinkM);
//    	assertEquals(HR3sourceToCompare, FlinkHR3sourceToCompare);
    	
//    	assertEquals(HR3Comparisons, FlinkHR3Comparisons);
    }

}
