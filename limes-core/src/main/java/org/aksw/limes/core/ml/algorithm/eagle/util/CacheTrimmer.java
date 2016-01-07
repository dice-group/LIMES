package org.aksw.limes.core.ml.algorithm.eagle.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.apache.log4j.Logger;

/**
 * @author Klaus Lyko
 *
 */
public class CacheTrimmer {

	Mapping reference = new MemoryMapping();
	/**
	 * Method to scale down a reference mapping given by an Oracle.
	 * Only the first <i>max</i> <code>Entries</code> are used. 
	 * @param pM Oracle holding all data.
	 * @param max
	 * @return <code>Mapping</code> scaled down to max entries.
	 */
	public static Mapping trimExamples(Mapping m, int max) {
		Mapping output = new MemoryMapping();
		
		HashMap<String, HashMap<String, Double>> map = m.getMap();
		int count = 0;
	
		for(Entry<String, HashMap<String, Double>> e : map.entrySet()) {
			if(count >= max)
				break;
			String key = e.getKey();
			HashMap<String, Double> value=e.getValue();
//			Set<String> vSet=value.keySet();
			output.add(key, value);
//			for(String match: vSet) {
//				System.out.println(key+" => "+match+" : "+value.get(match));
//			}
			
			count ++;
		}
		return output;
	}
	/**
	 * Standard implementation to get random training examples. Basic approach is to
	 * get a random set of source uris of the reference mapping and for each one target
	 * uri it is mapped to!
	 * @param m
	 * @param max
	 * @return
	 */
	public static Mapping trimExamplesRandomly(Mapping m, int max) {
		Mapping output = new MemoryMapping();

		while(output.size()<Math.min(max, m.size())) {			
			Random rand = new Random(System.currentTimeMillis());
			if(m.getMap().keySet().size()<=0) {// avoid empty keysets 
				continue;
			}
			String key = m.getMap().keySet().toArray(new String[0])[rand.nextInt(m.getMap().keySet().size())];
			Iterator<String> it = m.getMap().get(key).keySet().iterator();
			while(it.hasNext()){
				String target = it.next();
				if(!output.contains(key, target)) {
					output.add(key, target, m.getConfidence(key, target));
					break;
				} else {
					// nothing to do here
				}
			}
		}
		return output;
	}
	/**
	 * Another implementation to get a random training data of size max out of the reference
	 * mapping m. The approach here is to randomly select source URIs of m and for each add 
	 * ALL target URIs it is mapped to.
	 * @param m
	 * @param max
	 * @return
	 */
	public static Mapping getRandomTrainingData(Mapping m, int max) {
		Mapping output = new MemoryMapping();
		int breakPoint = Math.min(max, m.getMap().keySet().size());
		while(output.getMap().keySet().size()<breakPoint) {
			Random rand = new Random(System.currentTimeMillis());
			String key = m.getMap().keySet().toArray(new String[0])[rand.nextInt(m.getMap().keySet().size())];
			Iterator<String> it = m.getMap().get(key).keySet().iterator();
			while(it.hasNext()){
				String target = it.next();
				if(!output.contains(key, target)) {
					output.add(key, target, m.getConfidence(key, target));
				} else {
					continue;
				}
			}
		}
		return output;
	}
	
	
	/**
	 * Method to scale down the Caches used to perform entity matching upon.
	 * Scaling down is done according to the given reference mapping. Returns Caches
	 * only holding instances of the  reference Mapping.
	 * @param sC Cache for source data.
	 * @param tC Cache for target data.
	 * @param m Reference Mapping (e.g. part of the optimal mapping)
	 * @return Array holding both resulting Caches, where the Cache for the source is at index 0. Cache for the target knowledge base at index 1.
	 */
	public static Cache[] processData(Cache sC, Cache tC, Mapping m) {
		Logger logger = Logger.getLogger("LIMES");
		if(m.getSize()<=100)
			logger.info("Scaling Caches down to "+m);
		Cache[] ret = new Cache[2];
		Cache h1 = new MemoryCache();
		Cache h2 = new MemoryCache();
		HashMap<String, HashMap<String, Double>> map = m.getMap();
		
		for(Entry<String, HashMap<String, Double>> e : map.entrySet()) {
			String key = e.getKey();
			Instance i = sC.getInstance(key);
			if(i == null){
				logger.info("unable to find instance with key "+key);
				continue;
			}				
			h1.addInstance(i);
			
			HashMap<String, Double> value = e.getValue();
			for(Entry<String, Double> e2 : value.entrySet()) {
				Instance j = tC.getInstance(e2.getKey());
				//System.out.println(e2.getKey());
				if(j != null)
					h2.addInstance(j); 
				else 
					logger.info("unable to find instance with key "+e2.getKey());
			}
		}
		ret[0] = h1;
		ret[1] = h2;
		return ret;
	}
	
	public Cache[] processDataEqually(Cache hc1, Cache hc2, Mapping m, int numberOfQuestions) {
		reference.getMap().clear();
		Cache[] ret = new Cache[2];
		Cache h1 = new MemoryCache();
		Cache h2 = new MemoryCache();
		int countQuestions = 0;
		Mapping alreadyAsked = new MemoryMapping();
		
		ArrayList<String> uris1=hc1.getAllUris();
		ArrayList<String> uris2=hc2.getAllUris();
		
		while(countQuestions < numberOfQuestions) {
			Random random = new Random(System.currentTimeMillis());
			String uri1=uris1.get(random.nextInt(uris1.size()));
			String uri2=uris2.get(random.nextInt(uris2.size()));
			if(alreadyAsked.contains(uri1, uri2)) {
				continue;
			}
			h1.addInstance(hc1.getInstance(uri1));
			h2.addInstance(hc2.getInstance(uri2));
			countQuestions++;
			if(m.contains(uri1, uri2)) {
				reference.add(uri1, uri2, 1.0d);
			}				
		}
		ret[0] = h1;
		ret[1] = h2;
		Logger logger = Logger.getLogger("LIMES");
		logger.info("asking random "+numberOfQuestions+" questions got me "+reference.size()+" valid links");
		if(reference.getSize() < numberOfQuestions/2) {
				Mapping ref2 = trimExamplesRandomly(m, numberOfQuestions/2);
				Cache[] adding = processData(hc1, hc2, ref2);
				
				HashMap<String, HashMap<String, Double>> map = ref2.getMap();
				
				for(Entry<String, HashMap<String, Double>> e : map.entrySet()) {
					reference.add(e.getKey(), e.getValue());
				}
				
				for(Instance i : adding[0].getAllInstances()) {
					ret[0].addInstance(i);
				}
				for(Instance i : adding[1].getAllInstances()) {
					ret[1].addInstance(i);
				}
				logger.info("adding "+ref2.size()+" valid links and instances.");
		}			
		return ret;
	}
	
//	
//	public static void main(String[] args) {
//		String configFile = "Examples/GeneticEval/PublicationData.xml";
//		String file = "Examples/GeneticEval/Datasets/DBLP-ACM/DBLP-ACM_perfectMapping.csv";
//	
//		configFile = "Examples/GeneticEval/DBLP-Scholar.xml";
//		file = "Examples/GeneticEval/Datasets/DBLP-Scholar/DBLP-Scholar_perfectMapping.csv";
//		
//		Oracle o = OracleFactory.getOracle(file, "csv", "simple");
//		o.getMapping();
////		Mapping optimalMapping = o.getMapping();
//		//o.loadData(optimalMapping);
//		
//		CacheTrimmer trimmer = new CacheTrimmer();
//		
//		
//		
////		Mapping res = trimExamples(o, 10);
////		System.out.println(res);	
//		
//		ConfigReader cR = new ConfigReader();
//		cR.validateAndRead(configFile);
//		HybridCache sC = HybridCache.getData(cR.getSourceInfo());		
//		HybridCache rC = HybridCache.getData(cR.getTargetInfo());
//		HybridCache[] caches = trimmer.processDataEqually(sC, rC, o, 50);
//		System.out.println(trimmer.getReferenceMapping().size());
//		System.out.println("Cache 1: ");
//		System.out.println(caches[0].size());
//		System.out.println("Cache 2: ");
//		System.out.println(caches[1].size());
//	
//	}
//	
	public Mapping getReferenceMapping() {
		return reference;
	}
}
