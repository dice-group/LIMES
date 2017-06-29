package org.aksw.limes.core.ml.algorithm.eagle.coala;
/**
 * Wraps around Wekas cluster assignments, to identify most relevant instances in a cluster.
 * @author Klaus Lyko
 */
public class ClusterInstance implements Comparable<ClusterInstance>{
	
	int cluster = -1;
	int instanceNumber = -1;
	String uri1 = "";
	String uri2 = "";
	double clusterProbability = 0;
	
	public ClusterInstance(int instanceNumber, int clusterNumber, double clusterProbability ) {
		this.instanceNumber = instanceNumber;
		this.cluster = clusterNumber;
		this.clusterProbability = clusterProbability;
	}
	
	public ClusterInstance(int instanceNumber, String uri1, String uri2, int clusterNumber, double clusterProbability ) {
		this.instanceNumber = instanceNumber;
		this.uri1 = uri1;
		this.uri2 = uri2;
		this.cluster = clusterNumber;
		this.clusterProbability = clusterProbability;
	}
	
	@Override
	public int compareTo(ClusterInstance arg0) {
		return Double.compare(clusterProbability, arg0.clusterProbability);
	}
	
	@Override
	public String toString() {
		return ""+instanceNumber+" - "+clusterProbability;
	}

}
