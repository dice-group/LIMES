package org.aksw.limes.core.execution.planning.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class Plan implements IPlan {
    static Logger logger = Logger.getLogger("LIMES");
    public double runtimeCost;
    public double mappingSize;
    public double selectivity;
    public List<Instruction> instructionList;

    public Plan() {
	instructionList = new ArrayList<Instruction>();
	runtimeCost = 0d;
	mappingSize = 0d;
	selectivity = 1d;
    }
    @Override
    public void addInstruction(Instruction instruction) {
	boolean added = instructionList.add(instruction);
	if (!added)
	    logger.info("ExecutionPlan.addInstructiun() failed");
    }
    @Override
    public void removeInstruction(int i) {
	instructionList.remove(i);
    }
    @Override
    public void removeInstruction(Instruction i) {
	instructionList.remove(i);
    }
    @Override
    public List<Instruction> getInstructionList() {
	return instructionList;
    }
    /**
     * Checks whether the instructionList of the current NestedPlan is empty
     * 
     */
    public boolean isEmpty() {
	return instructionList.isEmpty();
    }
    
    /**
     * Returns the size of the instructionList of the current NestedPlan
     * 
     * @return Number of instructions in the instructionList
     */
    public int size() {
	return instructionList.size();
    }

}
