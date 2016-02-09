package org.aksw.limes.core.ml.algorithm.lion.graphic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.algorithm.lion.SearchTreeNode;
import org.apache.log4j.Logger;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;


/**
 * @author mofeed
 * @author Klaus Lyko
 *
 */
public class GraphGenerator {

	/**
	 * @param args
	 */
	public List<GraphNode> nodes = new ArrayList<GraphNode>();
	public List<GraphEdge> edges = new ArrayList<GraphEdge>();
	public Map<String,List<NodeHistory>> attributes = new HashMap<String,List<NodeHistory>>();
	
	public int nodesCount =1 ;
	static Logger logger = Logger.getLogger("LIMES");

	public void createGraphIterative(TreeSet<SearchTreeNode> treeNodes,SearchTreeNode root)
	{
		System.out.println(treeNodes.size());
		LinkedList<GraphNode> nodesQueue = new LinkedList<GraphNode>();
		String nodeId = "0";
		int edgeId = 0;
		
		GraphNode newNode = new GraphNode(nodeId, root); // create graph node representing the root with id =1
		nodesQueue.add(newNode); 		// add the first element into the queue which represents the root
		nodes.add(newNode); // add the new graph node to the graph's nodes list
		nodeId = String.valueOf(Integer.parseInt(nodeId)+1);
		int x=0,y=0;
		while(nodesQueue.size() > 0 ) // while elements exists in the queue
		{
			System.out.println("Dequeue a node from the queue");
			GraphNode parentNode = nodesQueue.poll(); // get the front node from the queue
			System.out.println("The parent node information are:" + parentNode.data.getSpec().toString());

			// iterate over the tree nodes to check the current node children
//			System.out.println("x:"+x++);
			for (SearchTreeNode node : treeNodes) {
/*				System.out.println("The parent node##########################:" + parentNode.data.getSpec().toString());
				System.out.println("The checked node ########################"+ node.getSpec().toString() );*/
//				System.out.println("y:"+y++);
//				if(y==2626 && x== 26)
//					y=2625;
				if(node.specificName.equals("root") || equality(node.getSpec(),parentNode.data.getSpec())) // the node was the root or will be compared to itself is itself
					continue;
				if(equality(node.getParent().getSpec(),parentNode.data.getSpec())) // one of its children is found
						{
							newNode = new GraphNode(nodeId, node); // create new graph node for the child
							nodesQueue.add(newNode);	//add the child into the queue to be checked for its own children later
							//newNode.start = String.valueOf(node.timestamp);
							nodes.add(newNode); // add the child to the list of nodes
							
							GraphEdge newEdge = new GraphEdge(edgeId,Integer.parseInt(parentNode.nodeId), Integer.parseInt(newNode.nodeId));
							//newEdge.start=String.valueOf(node.timestamp);
							edges.add(newEdge);
							
							attributes.put(newNode.nodeId, newNode.data.history);
							nodeId = String.valueOf(Integer.parseInt(nodeId)+1);
						}
						
			}
		}
	}
	
	
	public void createGraphRecursive(SearchTreeNode root)
	{
		GraphNode newNode = new GraphNode("0",root);
		newNode.start = String.valueOf(root.creationTime);
		nodes.add(newNode);
		//----------------------------------------
		attributes.put(newNode.nodeId, newNode.data.history);
		//--------------------------------------------
		createGraph2_aux(root,0,"0");
	}
	
	private void createGraph2_aux(SearchTreeNode parent,int level,String parentId)
	{
		level++;
		if(parent.getChildren().size() == 0)
			return;
		else
		{
			List<SearchTreeNode> children = parent.getChildren();
			for(int i=0;i<children.size();i++)
			{
				try {
					
					nodesCount++;
					SearchTreeNode child = children.get(i);
					String childId = String.valueOf(level)+String.valueOf(i)+String.valueOf(nodesCount);
					GraphNode newNode = new GraphNode(childId,child);
					//newNode.start = String.valueOf(child.timestamp);
					nodes.add(newNode);
					
					//------------may fail due to NumberFormatException--------------------
					GraphEdge newEdge = new GraphEdge(Long.parseLong(childId), Long.parseLong(parentId), Long.parseLong(childId));
					//newEdge.start=String.valueOf(child.timestamp);
					edges.add(newEdge);
					//----------------------------------------
					attributes.put(newNode.nodeId, newNode.data.history);
					//------------------------------------------------
					createGraph2_aux(child, level, childId);
				}catch(Exception e) {}
			}
		}
	}
	public void createGraphItersive(TreeSet<SearchTreeNode> tree)
	{
		defineNodes(tree);
		defineEdges(tree);
	}
	private void defineNodes(TreeSet<SearchTreeNode> tree)
	{
		Iterator<SearchTreeNode> it = tree.descendingIterator();
		while(it.hasNext()) {
			SearchTreeNode node = it.next();
			GraphNode newNode = new GraphNode(String.valueOf(node.nodeId),node);
			nodes.add(newNode);
		}
	}
	public void defineEdges(TreeSet<SearchTreeNode> tree)
	{
		int edgesCounter = 0 ;
		Map<Integer, Integer> existedEdge = new HashMap<Integer, Integer>();
		Iterator<SearchTreeNode> it = tree.descendingIterator();
				while(it.hasNext()) {
					SearchTreeNode node = it.next();
					NodeHistory previous=null,current=null;
					boolean cutEdges=false;
					for(NodeHistory nodehistory: node.history) // check for a moment where children turned to be 0
					{
						previous = current;
						current = nodehistory;
						if(previous != null && previous.children != 0 && current.children == 0)
						{
							cutEdges=true;
							break;
						}
					}
					for (SearchTreeNode child : node.getChildren()) {
						if(!(existedEdge.containsKey(node.nodeId) && existedEdge.containsKey(child.nodeId)))
						{
							GraphEdge edge = new GraphEdge(edgesCounter++, node.nodeId, child.nodeId);
							edge.start =String.valueOf(node.creationTime);
							edges.add(edge);
							if(cutEdges)
								edge.end = String.valueOf(current.historyTiming);
							existedEdge.put(node.nodeId, child.nodeId);
						}
						
					}
				}
	}
	public void displayGraph()
	{
		System.out.println("List of all existing nodes:\n");
		for (GraphNode node : nodes) {
			System.out.println(node);
		}
		System.out.println("List of all edges\n");
		for (GraphEdge edge : edges) {
			System.out.println(edge);
		}
		
	}
	public void writeGraph()
	{
		//String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		//String gefx =   "<gexf xmlns:viz=\"http:///www.gexf.net/1.1draft/viz\" version=\"1.1\" xmlns=\"http://www.gexf.net/1.1draft\">\n";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		//String meta =   "<meta lastmodifieddate="+date+">\n<\\meta>\n";
		
		
		String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
							"<gexf xmlns:viz=\"http:///www.gexf.net/1.1draft/viz\" version=\"1.1\" xmlns=\"http://www.gexf.net/1.1draft\">\n"+
							"<meta lastmodifieddate=\""+date+"\">\n"+
							"<creator>Gephi 0.7</creator>\n"+
							"</meta>\n"+
							"<graph defaultedgetype=\"directed\" idtype=\"string\" type=\"static\">\n";
		String nodesAttributes = 	"<attributes class=\"node\">\n"+
									"<attribute id=\"0\" title=\"score\" type=\"string\"/>\n"+
									"<attribute id=\"1\" title=\"level\" type=\"integer\"/>\n"+
									"</attributes>\n";
		String nodesTags = "<nodes count=\""+nodes.size()+"\">\n";
		for (GraphNode node : nodes) {
			if(node.data.specificName.equals("root"))
				nodesTags+="<node id=\""+node.nodeId+"\" label=\"root\">\n"+
						"\t<attvalue for=\"0\" value=\""+node.data.getScore()+"\"/>\n"+
						"\t<attvalue for=\"1\" value=\""+String.valueOf(node.nodeId).charAt(0)+"\"/>\n";
//			else if(node.data.getSpec().filterExpression == null)
//				nodesTags+="<node id=\""+node.nodeId+"\" label=\"null\">\n"+
//						"\t<attvalue for=\"0\" value=\""+node.data.getScore()+"\"/>\n"+
//						"\t<attvalue for=\"1\" value=\""+String.valueOf(node.nodeId).charAt(0)+"\"/>\n";
			else
				//nodesTags+="<node id=\""+node.nodeId+"\" label=\""+node.data.getSpec().filterExpression+":"+node.data.getScore()+"\"/>\n";
				nodesTags+="<node id=\""+node.nodeId+"\" label=\""+node.data.getSpec().toStringOneLine()+":"+node.data.getScore()+"\">\n"+
							"\t<attvalue for=\"0\" value=\""+node.data.getScore()+"\"/>\n"+
							"\t<attvalue for=\"1\" value=\""+String.valueOf(node.nodeId).charAt(0)+"\"/>\n";
			nodesTags+="</node>\n";
		}
		nodesTags+="</nodes>\n";
		
		String edgeTags = "<edges count=\""+edges.size()+"\">\n";
		for (GraphEdge edge : edges) {
			edgeTags+="<edge id=\""+edge.edgeId+"\" source=\""+edge.sourceNodeId+"\" target=\""+edge.targetNodeId+"\"/>\n";
			//edgeTags+="<edge id=\""+edge.edgeId+"\" source=\""+edge.sourceNodeId+"\" target=\""+edge.targetNodeId+"\" start=\""+edge.start+"\" end=\""+edge.end+"\"/>\n";
		}
		edgeTags+="</edges>\n";
		
		String end = "</graph>\n</gexf>\n";
		writeToFile("C:\\Users\\Lyko\\workspace\\LIMES\\resources\\results\\refgraph\\recursiverefineatrr2.gexf", start+nodesAttributes+nodesTags+edgeTags+end);
//		System.out.println(start+nodesTags+edgeTags+end);
		
	}
	public void writeGraphDynamically()
	{
		//String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		//String gefx =   "<gexf xmlns:viz=\"http:///www.gexf.net/1.1draft/viz\" version=\"1.1\" xmlns=\"http://www.gexf.net/1.1draft\">\n";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		//String meta =   "<meta lastmodifieddate="+date+">\n<\\meta>\n";
		
		
		/*String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
							"<gexf xmlns:viz=\"http:///www.gexf.net/1.1draft/viz\" version=\"1.1\" xmlns=\"http://www.gexf.net/1.1draft\">\n"+
							"<meta lastmodifieddate=\""+date+"\">\n"+
							"<creator>Gephi 0.7</creator>\n"+
							"</meta>\n"+
							"<graph mode=\"dynamic\" defaultedgetype=\"directed\" timeformat=\"date\">\n";*/
		String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<gexf xmlns=\"http://www.gexf.net/1.1draft\"\n" +
						"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
						"xsi:schemaLocation=\"http://www.gexf.net/1.1draft http://www.gexf.net/1.1draft/gexf.xsd\"\n"+
                         "version=\"1.1\">\n"+
                         "<graph mode=\"dynamic\" defaultedgetype=\"directed\">\n";
		
		String nodesAttributes = "<attributes class=\"node\" mode=\"dynamic\">\n"+
								 "<attribute id=\"score\" title=\"score\" type=\"double\"/>\n"+
								 "<attribute id=\"bestscore\" title=\"bestscore\" type=\"double\"/>\n"+
								 "<attribute id=\"expansion\" title=\"expansion\" type=\"string\"/>\n"+
								 "</attributes>\n";
		String nodeAttributes="";

		String nodesTags = "<nodes count=\""+nodes.size()+"\">\n";
		for (GraphNode node : nodes) {
			if(node.data.specificName.equals("root"))
				nodesTags+="<node id=\""+node.nodeId+"\" label=\"root\" start=\""+node.data.creationTime+"\">\n"/*+
						"\t<attvalue for=\"0\" value=\""+node.data.getScore()+"\"/>\n"+
						"\t<attvalue for=\"1\" value=\""+String.valueOf(node.nodeId).charAt(0)+"\"/>\n"*/;
//			else if(node.data.getSpec().filterExpression == null)
//				nodesTags+="<node id=\""+node.nodeId+"\" label=\""+node.data.getSpec().operator+"\" start=\""+node.data.creationTime+"\">\n"/*+
//						"\t<attvalue for=\"0\" value=\""+node.data.getScore()+"\"/>\n"+
//						"\t<attvalue for=\"1\" value=\""+String.valueOf(node.nodeId).charAt(0)+"\"/>\n"*/;
			else
				//nodesTags+="<node id=\""+node.nodeId+"\" label=\""+node.data.getSpec().filterExpression+":"+node.data.getScore()+"\"/>\n";
				nodesTags+="<node id=\""+node.nodeId+"\" label=\""+node.data.getSpec().toStringOneLine()+"\" start=\""+node.data.creationTime+"\">\n"/*+
							"\t<attvalue for=\"0\" value=\""+node.data.getScore()+"\"/>\n"+
							"\t<attvalue for=\"1\" value=\""+String.valueOf(node.nodeId).charAt(0)+"\"/>\n"*/;
			//adding the attributes for each node
			nodeAttributes= "<attvalues>\n";
			NodeHistory previousevent = null;	
			for (NodeHistory historyEvent : node.data.history) {
				if(previousevent == null)
				{
					nodeAttributes += "<attvalue for=\"score\" value=\""+historyEvent.score+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
					nodeAttributes += "<attvalue for=\"bestscore\" value=\""+historyEvent.bestScore+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
					nodeAttributes += "<attvalue for=\"expansion\" value=\""+historyEvent.expansion+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
					nodeAttributes += "<attvalue for=\"threshold\" value=\""+historyEvent.threshold+"\" start=\""+historyEvent.historyTiming+"\"/>\n";

				}
				else
				{
					if(historyEvent.score != previousevent.score)
						nodeAttributes += "<attvalue for=\"score\" value=\""+historyEvent.score+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
					if(historyEvent.bestScore != previousevent.bestScore)
						nodeAttributes += "<attvalue for=\"bestscore\" value=\""+historyEvent.bestScore+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
					if(historyEvent.expansion != previousevent.expansion)
						nodeAttributes += "<attvalue for=\"expansion\" value=\""+historyEvent.expansion+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
					if(historyEvent.threshold != previousevent.threshold)
						nodeAttributes += "<attvalue for=\"threshold\" value=\""+historyEvent.threshold+"\" start=\""+historyEvent.historyTiming+"\"/>\n";
				}
				previousevent = historyEvent;
			}
			nodeAttributes+=" </attvalues>\n";
			nodesTags+=nodeAttributes;
			nodesTags+="</node>\n";
		}
		nodesTags+="</nodes>\n";
		
		String edgeTags = "<edges count=\""+edges.size()+"\">\n";
		for (GraphEdge edge : edges) {
			edgeTags+="<edge id=\""+edge.edgeId+"\" source=\""+edge.sourceNodeId+"\" target=\""+edge.targetNodeId+"\"/>\n";
			//edgeTags+="<edge id=\""+edge.edgeId+"\" source=\""+edge.sourceNodeId+"\" target=\""+edge.targetNodeId+"\" start=\""+edge.start+"\" end=\""+edge.end+"\"/>\n";		
		}
		edgeTags+="</edges>\n";
		
		String end = "</graph>\n</gexf>\n";
		writeToFile("C:\\Users\\Lyko\\workspace\\LIMES\\resources\\results\\refgraph\\itersiveredynamic.gexf", start+nodesAttributes+nodesTags+edgeTags+end);
//		System.out.println(start+nodesTags+edgeTags+end);
		
	}
	private void writeToFile(String filePath, String content)
	{
		File file = new File(filePath);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private boolean equality(Object first,Object second) {
		LinkSpecification f = (LinkSpecification) first;
		LinkSpecification s = (LinkSpecification) second;
		int i=0;
		if(f.getThreshold() == 0.9025 && s.getThreshold() == 0.95)
			i=3;
    	List<LinkSpecification> children = new ArrayList<LinkSpecification>();
    		if(f.isAtomic() && s.isAtomic()) {
    			//mofeed:start
    			if(f.getFilterExpression() == null && s.getFilterExpression() == null)
    				return Math.abs(f.getThreshold()-s.getThreshold()) < 0.02d;
    			else if(f.getFilterExpression() != null && s.getFilterExpression() == null)
    				return false;
    			else if(f.getFilterExpression() == null && s.getFilterExpression() != null)
    				return false;
    			//mofeed:end

    			else if((f.getFilterExpression().equalsIgnoreCase(s.getFilterExpression())))
    				return Math.abs(f.getThreshold()-s.getThreshold()) < 0.01d; //that means similar in filter expression and threshold too
    		} else if((!f.isAtomic()) && (!s.isAtomic())) // both are not atomic, we need to check further
    			{
    			//compare each measure in the filter expression with the other
    			if(f.getOperator() == null && s.getOperator() == null)
    				return true;
    			else if((f.getOperator() != null && s.getOperator() == null) || (f.getOperator() == null && s.getOperator() != null))
    				return false;
    			if(f.getOperator().equals(s.getOperator())) {
//    				if(this.children.size()==o.children.size()) {
    					HashSet<LinkSpecification> hs = new HashSet<LinkSpecification>();
    					hs.addAll(children);
    					boolean b = hs.addAll(s.getChildren());
    					return(hs.addAll(s.getChildren()));//true if this collection changed as a result of the call
    			}
    		else // one is atomic and the other is not sure they are not the same
    			return false;
    		}
//    	}
    	return false;
    }

}
