package org.aksw.limes.core.io.mapping;

import org.junit.Before;
import org.junit.Test;

public class MappingMathTest {
    
    
    /**
     *  @author Sherif
     */
    
    AMapping m1 = MappingFactory.createDefaultMapping();
    AMapping m2 = MappingFactory.createDefaultMapping();
    
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
    }
    
    @Test
    public void testMultiply(){
        AMapping resultMap = MappingFactory.createDefaultMapping();
        resultMap.add("a1", "a3", 1.0);
        resultMap.add("b1", "b3", 1.0);
        
        assert(MappingMath.multiply(m1, m2, false).equals(resultMap));
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
        
        assert(MappingMath.add(m1, m2, true).equals(resultMap));
    }


}
