package org.aksw.limes.core.execution.planning.plan;


import static org.junit.Assert.*;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.junit.Test;

public class InstructionTest {

    @Test
    public void equals() {
	System.out.println("Equal");

	
	Instruction i = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction i2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);

	assertTrue(i.equals(i2));
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	Instruction i3 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
	assertTrue(!i.equals(i3));
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	Instruction i4 = new Instruction(Command.FILTER, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
	assertTrue(!i.equals(i4));
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	Instruction i5 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.45", -1, -1, 0);
	assertTrue(!i.equals(i5));
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	Instruction i6 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", 12, -1, 0);
	assertTrue(i.equals(i6));
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	Instruction i7 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
	i7.setMainThreshold("lol");
	assertTrue(!i.equals(i7));
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
    }

}
