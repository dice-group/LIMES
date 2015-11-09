package org.aksw.limes.core.measures.mapper;
import static org.junit.Assert.*;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.SetOperations;
import org.junit.Test;

public class SetOperationsTest {
    
    @Test
    public static void main(String args[]) {
	Mapping a = new MemoryMapping();
	Mapping b = new MemoryMapping();
	SetOperations opers = new SetOperations();
	a.add("c", "c", 0.5);
	a.add("a", "z", 0.5);
	a.add("a", "b", 0.5);
	b.add("a", "c", 0.5);
	b.add("a", "b", 0.7);
	b.add("b", "y", 0.7);
	System.out.println(opers.union(a, b));
    }

}
