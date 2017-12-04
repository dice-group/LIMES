package org.aksw.limes.core.measures.measure.graphs;

import org.junit.Assert;
import org.junit.Test;

public class NMSimilarityTest {

    @Test
    public void testSimilarity() {
        try {
            Integer[][] graphASource = new Integer[][]{{0, 1, 0},
                    {0, 0, 1},
                    {0, 0, 0}};

            Integer[][] graphBSource = new Integer[][]{{0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 1},
                    {0, 0, 0, 0, 0, 0}};

            Integer[][] graphCSource = new Integer[][]{{0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0}};

            Graph graphA = new Graph(graphASource);
            Graph graphB = new Graph(graphBSource);
            Graph graphC = new Graph(graphCSource);

            NMSimilarity similarityMeasure = new NMSimilarity(graphB,graphC,  0.0001);
            Double similarity = similarityMeasure.getGraphSimilarity();
            System.out.println("\nTwo graphs have " + similarity + "% of similarity");
            Assert.assertEquals(17.05, similarity, 0.01);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
