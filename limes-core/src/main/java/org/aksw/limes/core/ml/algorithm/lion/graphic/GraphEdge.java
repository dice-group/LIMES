package org.aksw.limes.core.ml.algorithm.lion.graphic;

public class GraphEdge {
	public long edgeId;
	public long sourceNodeId;
	public long targetNodeId;
	public String start="",end="";//specifies the start and end of the edge
	public GraphEdge (long l, long m, long n){
		this.edgeId=l;
		this.sourceNodeId = m;
		this.targetNodeId =  n;
	}
	@Override 
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(edgeId)+":"+String.valueOf(sourceNodeId)+":"+String.valueOf(targetNodeId);
	}
}
