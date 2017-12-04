package org.aksw.limes.core.measures.measure.graphs;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class NMSimilarity {
    private Graph graphA;
    private Graph graphB;
    private List<List<Integer>> inNodeListA;
    private List<List<Integer>> outNodeListA;
    private List<List<Integer>> inNodeListB;
    private List<List<Integer>> outNodeListB;
    private Double[][] nodeSimilarity;
    private Double[][] inNodeSimilarity;
    private Double[][] outNodeSimilarity;
    private Double epsilon;
    private int graphSizeA;
    private int graphSizeB;

    public NMSimilarity(Graph graphA, Graph graphB, Double epsilon) {
        try {
            this.graphA = graphA;
            this.graphB = graphB;
            this.epsilon = epsilon;
            this.inNodeListA = graphA.getInDegreeNodeList();
            this.outNodeListA = graphA.getOutDegreeNodeList();
            this.inNodeListB = graphB.getInDegreeNodeList();
            this.outNodeListB = graphB.getOutDegreeNodeList();

            this.graphSizeA = graphA.getGraphSize();
            this.graphSizeB = graphB.getGraphSize();

            this.nodeSimilarity = new Double[graphSizeA][graphSizeB];
            this.inNodeSimilarity = new Double[graphSizeA][graphSizeB];
            this.outNodeSimilarity = new Double[graphSizeA][graphSizeB];

            initializeSimilarityMatrices();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeSimilarityMatrices() {
        for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) {
                Double maxDegree = Double.valueOf(Math.max(inNodeListA.get(i).size(), inNodeListB.get(j).size()));
                if (maxDegree != 0) {
                    inNodeSimilarity[i][j] = ((Math.min(inNodeListA.get(i).size(), inNodeListB.get(j).size())) / (maxDegree));
                } else {
                    inNodeSimilarity[i][j] = Double.valueOf(0);
                }

                maxDegree = Double.valueOf(Math.max(outNodeListA.get(i).size(), outNodeListB.get(j).size()));
                if (maxDegree != 0) {
                    outNodeSimilarity[i][j] = ((Math.min(outNodeListA.get(i).size(), outNodeListB.get(j).size())) / (maxDegree));
                } else {
                    outNodeSimilarity[i][j] = Double.valueOf(0);
                }
            }
        }

        for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) {
                System.out.print(inNodeSimilarity[i][j] + " ");
                nodeSimilarity[i][j] = (inNodeSimilarity[i][j] + outNodeSimilarity[i][j]) / 2;
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) {
                System.out.print(outNodeSimilarity[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) {
                System.out.print(nodeSimilarity[i][j]+" ");
            }
            System.out.println();
        }

    }

    public void measureSimilarity() {
        double maxDifference = 0.0;
        boolean terminate = false;

        while (!terminate) {
            maxDifference = 0.0;
            for (int i = 0; i < graphSizeA; i++) {
                for (int j = 0; j < graphSizeB; j++) {
                    //calculate in-degree similarities
                    double similaritySum = 0.0;
                    double maxDegree = Double.valueOf(Math.max(inNodeListA.get(i).size(), inNodeListB.get(j).size()));
                    int minDegree = Math.min(inNodeListA.get(i).size(), inNodeListB.get(j).size());
                    if (minDegree == inNodeListA.get(i).size()) {
                        similaritySum = enumerationFunction(inNodeListA.get(i), inNodeListB.get(j), 0);
                    } else {
                        similaritySum = enumerationFunction(inNodeListB.get(j), inNodeListA.get(i), 1);
                    }
                    if (maxDegree == 0.0 && similaritySum == 0.0) {
                        inNodeSimilarity[i][j] = 1.0;
                    } else if (maxDegree == 0.0) {
                        inNodeSimilarity[i][j] = 0.0;
                    } else {
                        inNodeSimilarity[i][j] = similaritySum / maxDegree;
                    }

                    //calculate out-degree similarities
                    similaritySum = 0.0;
                    maxDegree = Double.valueOf(Math.max(outNodeListA.get(i).size(), outNodeListB.get(j).size()));
                    minDegree = Math.min(outNodeListA.get(i).size(), outNodeListB.get(j).size());
                    if (minDegree == outNodeListA.get(i).size()) {
                        similaritySum = enumerationFunction(outNodeListA.get(i), outNodeListB.get(j), 0);
                    } else {
                        similaritySum = enumerationFunction(outNodeListB.get(j), outNodeListA.get(i), 1);
                    }
                    if (maxDegree == 0.0 && similaritySum == 0.0) {
                        outNodeSimilarity[i][j] = 1.0;
                    } else if (maxDegree == 0.0) {
                        outNodeSimilarity[i][j] = 0.0;
                    } else {
                        outNodeSimilarity[i][j] = similaritySum / maxDegree;
                    }

                }
            }

            for (int i = 0; i < graphSizeA; i++) {
                for (int j = 0; j < graphSizeB; j++) {
                    double temp = (inNodeSimilarity[i][j] + outNodeSimilarity[i][j]) / 2;
                    if (Math.abs(nodeSimilarity[i][j] - temp) > maxDifference) {
                        maxDifference = Math.abs(nodeSimilarity[i][j] - temp);
                    }
                    nodeSimilarity[i][j] = temp;
                }
            }

            if (maxDifference < epsilon) {
                terminate = true;
            }
        }
        DecimalFormat f = new DecimalFormat("0.000");

        for (int i = 0; i < graphSizeA; i++) {
            for (int j = 0; j < graphSizeB; j++) {
                nodeSimilarity[i][j] = Double.valueOf(f.format(nodeSimilarity[i][j]));
                System.out.print(nodeSimilarity[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public double enumerationFunction(List<Integer> neighborListMin, List<Integer> neighborListMax, int graph) {
        double similaritySum = 0.0;
        Map<Integer, Double> valueMap = new HashMap<Integer, Double>();
        if (graph == 0) {
            for (int i = 0; i < neighborListMin.size(); i++) {
                int node = neighborListMin.get(i);
                double max = 0.0;
                int maxIndex = -1;
                for (int j = 0; j < neighborListMax.size(); j++) {
                    int key = neighborListMax.get(j);
                    if (!valueMap.containsKey(key)) {
                        if (max < nodeSimilarity[node][key]) {
                            max = nodeSimilarity[node][key];
                            maxIndex = key;
                        }
                    }
                }
                valueMap.put(maxIndex, max);
            }
        } else {
            for (int i = 0; i < neighborListMin.size(); i++) {
                int node = neighborListMin.get(i);
                double max = 0.0;
                int maxIndex = -1;
                for (int j = 0; j < neighborListMax.size(); j++) {
                    int key = neighborListMax.get(j);
                    if (!valueMap.containsKey(key)) {
                        if (max < nodeSimilarity[key][node]) {
                            max = nodeSimilarity[key][node];
                            maxIndex = key;
                        }
                    }
                }
                valueMap.put(maxIndex, max);
            }
        }

        for (double value : valueMap.values()) {
            similaritySum += value;
        }
        return similaritySum;
    }

    public Double getGraphSimilarity() {
        Double finalGraphSimilarity = 0.0;
        DecimalFormat f = new DecimalFormat("0.000");
        measureSimilarity();

        if (graphA.getGraphSize() < graphB.getGraphSize()) {
            finalGraphSimilarity = enumerationFunction(graphA.getNodeList(), graphB.getNodeList(), 0) / graphA.getGraphSize();
        } else {
            finalGraphSimilarity = enumerationFunction(graphB.getNodeList(), graphA.getNodeList(), 1) / graphB.getGraphSize();
        }
        finalGraphSimilarity = Double.valueOf(f.format(finalGraphSimilarity*100));
        return finalGraphSimilarity;
    }

}
