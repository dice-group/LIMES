package org.aksw.limes.core.measures.mapper.topology;

import static org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper.getPoints;

import java.util.Arrays;
import java.util.HashSet;

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class RADONTest {

    private static final Logger logger = LoggerFactory.getLogger(RADONTest.class);
    @Test
    public void test() {

        String polygonA, polygonB, relation;

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = RADON.DISJOINT;
        logger.info("{}","Test 1: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = RADON.DISJOINT;
        logger.info("{}","Test 2: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = RADON.DISJOINT;
        logger.info("{}","Test 3: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = RADON.EQUALS;
        logger.info("{}","Test 4: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = RADON.INTERSECTS;
        logger.info("{}","Test 5: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        polygonB = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        relation = RADON.CONTAINS;
        logger.info("{}","Test 6: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        polygonB = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        relation = RADON.WITHIN;
        logger.info("{}","Test 7: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((1 1, 3 1, 3 3, 1 3, 1 1))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";
        relation = RADON.OVERLAPS;
        logger.info("{}","Test 8: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 2, 2 3, 3 3, 3 2, 0 2))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";
        relation = RADON.TOUCHES;
        logger.info("{}","Test 9: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

    }

}
