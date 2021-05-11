/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.measures.mapper.topology;

import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper.getPoints;

public class RADONTest {

    @Test
    public void test() {

        String polygonA, polygonB, relation;

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = RADON.DISJOINT;
        System.out.println("Test 1: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((2 2, 3 2, 3 3, 2 3, 2 2))";
        relation = RADON.DISJOINT;
        System.out.println("Test 2: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = RADON.DISJOINT;
        System.out.println("Test 3: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = RADON.EQUALS;
        System.out.println("Test 4: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        polygonB = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
        relation = RADON.INTERSECTS;
        System.out.println("Test 5: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        polygonB = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        relation = RADON.CONTAINS;
        System.out.println("Test 6: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((1 1, 2 1, 2 2, 1 2, 1 1))";
        polygonB = "POLYGON ((0 0, 3 0, 3 3, 0 3, 0 0))";
        relation = RADON.WITHIN;
        System.out.println("Test 7: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((1 1, 3 1, 3 3, 1 3, 1 1))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";
        relation = RADON.OVERLAPS;
        System.out.println("Test 8: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

        polygonA = "POLYGON ((0 2, 2 3, 3 3, 3 2, 0 2))";
        polygonB = "POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))";
        relation = RADON.TOUCHES;
        System.out.println("Test 9: " + (((RADON.getMapping((new HashSet<>(Arrays.asList(new Polygon("A", getPoints(polygonA))))), (new HashSet<>(Arrays.asList(new Polygon("Β", getPoints(polygonB))))), relation).getSize() != 0)) ? (polygonA + " " + relation + " " + polygonB) : "No Mapping."));

    }

}
