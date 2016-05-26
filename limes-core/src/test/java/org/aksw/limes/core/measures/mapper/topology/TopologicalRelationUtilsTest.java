package org.aksw.limes.core.measures.mapper.topology;

import static org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper.getPoints;

import java.util.Arrays;
import java.util.HashSet;

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;

public class TopologicalRelationUtilsTest {
    
    @Test
    public void test() {

        String polygonA, polygonB, relation;
        
        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = TopologicalRelationUtils.DISJOINT;
        TopologicalRelationUtils.theta = 10;
        System.out.println("Test 1: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = TopologicalRelationUtils.DISJOINT;
        TopologicalRelationUtils.theta = 1000;
        System.out.println("Test 2: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));
        
        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = TopologicalRelationUtils.DISJOINT;
        System.out.println("Test 3: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = TopologicalRelationUtils.EQUALS;
        System.out.println("Test 4: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = TopologicalRelationUtils.INTERSECTS;
        System.out.println("Test 5: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        polygonB = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        relation = TopologicalRelationUtils.CONTAINS;
        System.out.println("Test 6: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        polygonB = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        relation = TopologicalRelationUtils.WITHIN;
        System.out.println("Test 7: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));        

        polygonA = "POLYGON ((1 1, 3 1, 3 3, 1 3, 1 1))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";                
        relation = TopologicalRelationUtils.OVERLAPS;
        System.out.println("Test 8: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 2, 2 3, 3 3, 3 2, 0 2))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";                
        relation = TopologicalRelationUtils.TOUCHES;
        System.out.println("Test 9: " + (((TopologicalRelationUtils.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));        
    
    }

}
