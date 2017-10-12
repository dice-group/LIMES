package org.aksw.limes.core.io.query;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.junit.Test;

public class FlinkCSVQueryModuleTest {

	@Test
	public void test(){
		KBInfo kb = new KBInfo();
		kb.addProperty("title");
		kb.addProperty("director");
		kb.setEndpoint(Thread.currentThread().getContextClassLoader().getResource("datasets/dbpedia-linkedmdb/source2.csv").getPath());
		HashMap<String,Map<String,String>> functions = new HashMap<>();
		HashMap<String,String> titleMap = new HashMap<>();
		titleMap.put("title",null);
		functions.put("title",titleMap);
		HashMap<String,String> directorMap = new HashMap<>();
		directorMap.put("director",null);
		functions.put("director",directorMap);
		kb.setFunctions(functions);
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		FlinkCSVQueryModule f = new FlinkCSVQueryModule(kb, env, ",");
		List<Instance> resF = null;
		try {
			resF = f.readFileToDataSet().collect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CsvQueryModule c = new CsvQueryModule(kb);
		HybridCache cache = new HybridCache();
		c.fillCache(cache);
		List<Instance> resC = cache.getAllInstances();
		assertEquals(resF.size(), resC.size());
		for(Instance fI : resF){
			boolean contained = false;
			for(Instance cI : resC){
				if(fI.getUri().equals(cI.getUri())){
					contained = true;
					assertEquals(fI.getProperties(), cI.getProperties());
				}
			}
			if(!contained){
				System.out.println("Not contained: " + fI);
				assertTrue(false);
			}
		}
	}
}
