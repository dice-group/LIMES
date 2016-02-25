package org.aksw.limes.core.ml.algorithm.lion.graphic;

import org.aksw.limes.core.ml.algorithm.lion.SearchTreeNode;

/**
 * @author mofeed
 * @author Klaus Lyko
 *
 */
public class GraphNode {

	/**
	 * @param args
	 */
	public String nodeId;
	public SearchTreeNode data;
	public String start="",end="";
	boolean vistited = false;
	public GraphNode(String nodeid, SearchTreeNode data)
	{
		this.nodeId=nodeid;
		this.data=data;
	}

}
