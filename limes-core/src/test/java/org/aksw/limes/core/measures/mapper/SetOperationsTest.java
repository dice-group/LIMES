package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Test;

public class SetOperationsTest {

    @Test
    public void test() {
        AMapping a = MappingFactory.createDefaultMapping();
        AMapping b = MappingFactory.createDefaultMapping();
        a.add("c", "c", 0.5);
        a.add("a", "z", 0.5);
        a.add("a", "d", 0.5);

        b.add("a", "c", 0.5);
        b.add("a", "b", 0.7);
        b.add("b", "y", 0.7);
        assertTrue(MappingOperations.union(a, b) != null);
        assertTrue(MappingOperations.intersection(a, b).size() == 0);
        assertTrue(MappingOperations.difference(a, b).size() != 0);
        //assertTrue(MappingOperations.xor(a, b).size() != 0);
    }

}
