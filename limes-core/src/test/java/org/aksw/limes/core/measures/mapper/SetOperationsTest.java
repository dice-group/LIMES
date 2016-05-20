package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.*;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.junit.Test;

public class SetOperationsTest {
    
    @Test
    public void test() {
	Mapping a = new MemoryMapping();
	Mapping b = new MemoryMapping();
	a.add("c", "c", 0.5);
	a.add("a", "z", 0.5);
	a.add("a", "d", 0.5);
	
	b.add("a", "c", 0.5);
	b.add("a", "b", 0.7);
	b.add("b", "y", 0.7);
	assertTrue(MappingOperations.union(a, b)!= null);
	assertTrue(MappingOperations.intersection(a, b).size() == 0);
	assertTrue(MappingOperations.difference(a, b).size() != 0);
	assertTrue(MappingOperations.xor(a, b).size() != 0);
    }

}
