package org.aksw.limes.core.measures.measure.graphs;

import java.util.ArrayList;
import java.util.List;


public class Graph {
    private Integer[][] graph;
    private int graphSize;
    private List<Integer> nodeList;
    private List<List<Integer>> inDegreeNodeList;
    private List<List<Integer>> outDegreeNodeList;

    public Graph(Integer[][] graph) throws Exception {
        try {
            this.graph = graph;
            this.graphSize = graph.length;
            this.nodeList = new ArrayList<Integer>();
            this.inDegreeNodeList = new ArrayList<List<Integer>>();
            this.outDegreeNodeList = new ArrayList<List<Integer>>();
            setDegreeNodeList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void setDegreeNodeList() {
        for (int i=0; i<graphSize ; i++){
            inDegreeNodeList.add(new ArrayList<Integer>());
            outDegreeNodeList.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < graphSize; i++) {
            for (int j = 0; j < graphSize; j++) {
                if (graph[i][j] != 0){
                    inDegreeNodeList.get(j).add(i);
                    outDegreeNodeList.get(i).add(j);
                }
            }
        }
    }

    public Integer[][] getGraph() {
        return graph;
    }

    public int getGraphSize() {
        return graphSize;
    }

    public List<List<Integer>> getInDegreeNodeList() {
        return inDegreeNodeList;
    }

    public List<List<Integer>> getOutDegreeNodeList() {
        return outDegreeNodeList;
    }

    public List<Integer> getNodeList() {
        setNodeList();
        return nodeList;
    }

    public void setNodeList() {
        for(int i=0; i<graphSize;i++){
            nodeList.add(i);
        }
    }
}
