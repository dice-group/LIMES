package org.aksw.limes.core.measures.mapper.atomic.topology;

import static org.aksw.limes.core.measures.mapper.atomic.OrchidMapper.getPoints;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;
import org.aksw.limes.core.measures.mapper.atomic.topology.TopologicalRelationUtils;
import org.junit.Test;

public class TopologicalRelationUtilsTest {
    
    @Test
    public void test() {
	TopologicalRelationUtils t = new TopologicalRelationUtils();

        String polygonA, polygonB, relation;
        
        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = t.DISJOINT;
        t.theta = 10;
        System.out.println("Test 1: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = t.DISJOINT;
        t.theta = 1000;
        System.out.println("Test 2: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));
        
        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = t.DISJOINT;
        System.out.println("Test 3: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = t.EQUALS;
        System.out.println("Test 4: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = t.INTERSECTS;
        System.out.println("Test 5: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        polygonB = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        relation = t.CONTAINS;
        System.out.println("Test 6: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        polygonB = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        relation = t.WITHIN;
        System.out.println("Test 7: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));        

        polygonA = "POLYGON ((1 1, 3 1, 3 3, 1 3, 1 1))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";                
        relation = t.OVERLAPS;
        System.out.println("Test 8: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 2, 2 3, 3 3, 3 2, 0 2))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";                
        relation = t.TOUCHES;
        System.out.println("Test 9: " + (((t.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).size != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));        
    
    }

}
