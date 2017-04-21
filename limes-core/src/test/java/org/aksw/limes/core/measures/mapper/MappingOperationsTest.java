package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

public class MappingOperationsTest {
    
    AMapping m1 = MappingFactory.createDefaultMapping();
    AMapping m2 = MappingFactory.createDefaultMapping();
    AMapping m3 = MappingFactory.createDefaultMapping();
    AMapping m4 = MappingFactory.createDefaultMapping();
    
    @Before
    public void init() {
        m1.add("a1", "a2", 0.1);
        m1.add("b1", "b2", 0.1);
        m1.add("c1", "c2", 0.1);
        m1.add("d1", "d2", 0.1);

        m2.add("a2", "a3", 0.1);
        m2.add("b2", "b3", 0.1);
        m2.add("x2", "x3", 0.1);
        m2.add("y2", "y3", 0.1);
        
        m3.add("c", "c", 0.5);
        m3.add("a", "z", 0.5);
        m3.add("a", "d", 0.5);

        m4.add("a", "c", 0.5);
        m4.add("a", "b", 0.7);
        m4.add("b", "y", 0.7);
        
    }
    
    @Test
    public void testMultiply(){
        AMapping resultMap = MappingFactory.createDefaultMapping();
        resultMap.add("a1", "a3", 1.0);
        resultMap.add("b1", "b3", 1.0);
        
        assert(MappingOperations.multiply(m1, m2, false).equals(resultMap));
    }
    
    @Test
    public void testAdd(){
        AMapping resultMap = MappingFactory.createDefaultMapping();
        resultMap.add("a1", "a2", 0.1);
        resultMap.add("b2", "b3", 0.1);
        resultMap.add("a2", "a3", 0.1);
        resultMap.add("y2", "y3", 0.1);
        resultMap.add("x2", "x3", 0.1);
        resultMap.add("d1", "d2", 0.1);
        resultMap.add("c1", "c2", 0.1);
        resultMap.add("b1", "b2", 0.1);
        
        assert(MappingOperations.add(m1, m2, true).equals(resultMap));
    }

    @Test
    public void testUninon() {
        assertTrue(MappingOperations.union(m3, m4) != null);
    }
    
    @Test
    public void testIntersection() {
        assertTrue(MappingOperations.intersection(m3, m4).size() == 0);
    }
    
    @Test
    public void testDifference() {
        assertTrue(MappingOperations.difference(m3, m4).size() != 0);
    }

}
