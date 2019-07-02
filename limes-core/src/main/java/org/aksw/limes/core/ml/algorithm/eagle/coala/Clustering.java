package org.aksw.limes.core.ml.algorithm.eagle.coala;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.phoneticmeasure.SoundexMeasure;
import org.aksw.limes.core.measures.measure.string.StringMeasure;
import org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider.Triple;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.apache.log4j.Logger;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
/**
 * 
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public class Clustering {

	static Logger logger = Logger.getLogger(Clustering.class);
	
	ACache sourceCache;
	ACache targetCache;
//	LinkSpecGeneticLearnerConfig m_config;
    PropertyMapping propMap;
    AMapping reference;
    
    StringMeasure measure = new SoundexMeasure(); 
    
    double threshold = 0.6;
	public Clustering(ACache sourceCache, ACache targetCache, PropertyMapping propMap) {
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
//		this.m_config = m_config;
		this.propMap = propMap;
	}
 
    
    
	/**
	 * We first have to compute a similarity space for each candidate. That is, we 
	 * compute a out of a list of most controversy candidate links (Triples, value close to 0.5).
	 * a vector of similarity values.
	 * E.g. for every String property.
	 * @param candidates Triples given by a committee, are source - target URIs and a double value
	 * 			giving information if its regarded as a match.
	 */
	public Instances computeSimilaritySpace(List<Triple> candidates) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(propMap.stringPropPairs.size()+1);
		attributes.add(new Attribute("match"));
		for(PairSimilar<String> pair : propMap.stringPropPairs) {
			attributes.add(new Attribute(""+pair.a+"-"+pair.b));
		}
		Instances data =  new Instances("dataset", attributes, candidates.size()*propMap.stringPropPairs.size());
		
	
		// for each source URI - target URI
		for(Triple t : candidates) {
			double values[] = new double[attributes.size()];
			String debug = "Triple ("+t.toString()+")";
			values[0] = t.getSimilarity(); // regarded as match by committee?
			debug+="\t"+values[0];
			int i=1;
			// for each Property pair between source and target (eg. name - fullname) 
			for(PairSimilar<String> pair : propMap.stringPropPairs) {
				// compute a similarity score for the similarity vector
				values[i] = getSimilarity(measure, pair, t);
				debug+="\t"+values[i];
				i++;
			}
			Instance inst = new DenseInstance(1.0, values);
			
			boolean added = data.add(inst);
			logger.debug("new Inst:"+debug+" added?"+added);
		}
		return data;
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public static void wekaTest() throws Exception {
		Attribute uri1 = new Attribute("uri1", (ArrayList<String>) null);
		Attribute uri2 = new Attribute("uri2", (ArrayList<String>) null);
		Attribute dist1 = new Attribute("dist1");
		Attribute dist2 = new Attribute("dist2");
		
		
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("yes");
		labels.add("no");
		Attribute classAttr = new Attribute("class", labels);		
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(uri1);
		attributes.add(uri2);
		attributes.add(dist1);
		attributes.add(dist2);
//		attributes.add(classAttr);
		Instances dataset = new Instances("test-dataset", attributes, 0);
		
		int j = 200;
		for(int i = 0; i<100; i++) {
			double[] values = new double[4];
			values[0] = dataset.attribute(0).addStringValue("uri1_"+i);
			values[1] = dataset.attribute(1).addStringValue("uri2_"+j);
			values[2] = (double)i/(double)j;
			Random rand = new Random();
			values[3] = values[2]*rand.nextDouble();
//			if(values[0]>0.4)
//				values[1]=dataset.attribute(1).indexOfValue("yes");
//			else
//				values[1]=dataset.attribute(1).indexOfValue("no");
			Instance inst = new DenseInstance(1.0, values);
			dataset.add(inst);
			j--;
 		}
				
		Remove filter = new Remove();
		filter.setAttributeIndices("1,2");
		filter.setInputFormat(dataset);
		 
		 Instances dataClusterer = Filter.useFilter(dataset, filter);
		 
//		 for(int i = 0; i<dataClusterer.size(); i++) {
//			System.out.println(dataset.get(i)+" --- "+dataClusterer.get(i));
//		 }
//			 
		 
		 // train clusterer
		 EM clusterer = new EM();
		 // set further options for EM, if necessary...
		 clusterer.buildClusterer(dataClusterer);

		    // evaluate clusterer
		    ClusterEvaluation eval = new ClusterEvaluation();
		    eval.setClusterer(clusterer);
		    eval.evaluateClusterer(dataClusterer);
		    
//		    double[] clusters = eval.getClusterAssignments(); //for each instance nr of cluster
//		    for(int i = 0; i<clusters.length; i++) {
//		    	System.out.println(clusters[i]);
//		    }
		    
		   
		    
		    // print results
		    System.out.println(eval.clusterResultsToString());
		
		    
//		    //membership probabilities foreach cluster
//		    double[] distri = clusterer.distributionForInstance(dataClusterer.firstInstance());
//		    for(double d:distri)
//		    	System.out.println(d);
		 // output predictions
		    System.out.println("# - cluster - distribution");
		    
		    // init groups
		    ArrayList<ArrayList<ClusterInstance>> group = new ArrayList<ArrayList<ClusterInstance>>(eval.getNumClusters());
		    for(int i = 0; i<eval.getNumClusters(); i++) {
		    	group.add( new ArrayList<ClusterInstance>() );
		    }
		    // add all instances
		    for (int i = 0; i < dataClusterer.numInstances(); i++) {
		 
		    	int cluster = clusterer.clusterInstance(dataClusterer.instance(i));
		    	double[] dist = clusterer.distributionForInstance(dataClusterer.instance(i));
		    	
		    	ClusterInstance ci = new ClusterInstance(i, cluster, dist[cluster]);
		    	group.get(cluster).add(ci);
		    	System.out.print((i+1));
		    	System.out.print(" - ");
		    	System.out.print(cluster);
		    	System.out.print(" - ");
		    	System.out.print(Utils.arrayToString(dist));
		    	System.out.println();
		    }
		    int cl = 0;
		    
		    for(ArrayList<ClusterInstance> al: group) {
		    	Collections.sort(al);
		    	System.out.println("Cluster "+cl);
		    	for(ClusterInstance ci : al)
		    		System.out.println("\t"+ci);
		    	cl++;
		    }
		    
		    int k = 10;
		    List<Integer> representatives = new ArrayList<Integer>();
		    int clusterIndex = 0;
		    for(int i = 0; i<Math.min(k, dataset.size()); i++) {
		    	ArrayList<ClusterInstance> cluster = group.get(clusterIndex%group.size());
		    	for(int r = cluster.size()-1; r>=0; r--) {
		    		ClusterInstance inst = cluster.get(r);
		    		if(!representatives.contains(inst.instanceNumber)) {
		    			representatives.add(inst.instanceNumber);
		    			break;
		    		}
		    	}
		    	clusterIndex++;
		    }
		   
		    
		    System.out.println("k most representatives..."+representatives);
//		    for(int a : )
		    
//		     select k out of c clusters 
//		    
//		    
		    // need to sort representatives in each cluster
		    
		    
	}
	
//	/**
//	 * 
//	 * @param pair
//	 * @param t
//	 * @return
//	 */
//	private double similarity(PairSimilar<String> pair, Triple t) {
//		ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT,
//				sourceCache, targetCache, this.m_config.source.getVar(), this.m_config.target.getVar());
//		IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT,
//				sourceCache, targetCache);
//		StringBuffer expr = new StringBuffer("cosine");
//        expr.append("(");
//        expr.append(m_config.getExpressionProperty("source", pair.a));
//        expr.append(",");
//        expr.append(m_config.getExpressionProperty("target", pair.b));
//        expr.append(")");
//        expr.append("|");
//        expr.append(new BigDecimal(threshold).setScale(4, BigDecimal.ROUND_HALF_EVEN));
//		
//		LinkSpecification spec = new LinkSpecification(expr.toString(), threshold);
//		
//		AMapping map = engine.execute(spec, planner);
//		
//		GoldStandard goldStandard = new GoldStandard(reference);
//		IQualitativeMeasure measure = new FMeasure();
//	    double quality = measure.calculate(map, goldStandard);
//	    return quality;
//	}
	
	/**
	 * Method to compute an atomic similarity of a given source and target instance (Triple t)
	 * based upon a StringMeasure and a given PropertyPair (pair); 
	 * @param measure
	 * @param pair
	 * @param t
	 * @return
	 */
	private double getSimilarity(AMeasure measure, PairSimilar<String> pair, Triple t) {
		double sim = 0;
		sim = measure.getSimilarity(sourceCache.getInstance(t.getSourceUri()).getProperty(pair.a), 
				targetCache.getInstance(t.getTargetUri()).getProperty(pair.b));
		return sim;
	}
	
	
	public static void main(String args[]) throws Exception {
		wekaTest();
//		
//		Clustering cluster = new Clustering();
//		List<Triple> cand = new ArrayList<Triple>();
//		
//		Triple t = new Triple(null, null, threshold);
//		
//		
//		cluster.computeSimilaritySpace(candidates)
//		
	}
	
	
}
