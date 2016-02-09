package org.aksw.limes.core.execution.planning.plan;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.apache.log4j.Logger;

public class Plan implements IPlan {
    static Logger logger = Logger.getLogger("LIMES");
    protected double runtimeCost;
    protected double mappingSize;
    protected double selectivity;
    protected List<Instruction> instructionList;

    public Plan() {
	instructionList = new ArrayList<Instruction>();
	runtimeCost = 0d;
	mappingSize = 0;
	selectivity = 1d;

    }

    @Override
    public void addInstruction(Instruction instruction) {
	if (instruction != null) {
	    boolean added = instructionList.add(instruction);
	    if (!added)
		logger.info("ExecutionPlan.addInstructiun() failed");
	}

    }

    @Override
    public void removeInstruction(int i) {
	if (i >= getInstructionList().size() || i < 0)
	    logger.info("ExecutionPlan.removeInstructiun() failed");
	else
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
    @Override
    public boolean equals(Object other) {
	Plan o = (Plan) other;
	if (o == null)
	    return false;
	return (this.instructionList.equals(o.instructionList));
	 
    }
   
    /**
     * Generates a clone of the current NestedPlan
     * 
     * @return Clone of current NestedPlan
     */
    public Plan clone() {
	Plan clone = new Plan();

	// clone primitives fields
	clone.setMappingSize(this.mappingSize);
	clone.setRuntimeCost(this.runtimeCost);
	clone.setSelectivity(this.selectivity);

	// clone instructionList
	if (this.instructionList != null) {
	    if (this.instructionList.isEmpty() == false) {
		List<Instruction> cloneInstructionList = new ArrayList<Instruction>();
		for (Instruction i : this.instructionList) {
		    cloneInstructionList.add(i.clone());
		}
		clone.setInstructionList(cloneInstructionList);
	    } else {
		clone.setInstructionList(new ArrayList<Instruction>());
	    }
	} else
	    clone.setInstructionList(null);

	return clone;
    }

    /**
     * Returns the size of the instructionList of the current NestedPlan
     * 
     * @return Number of instructions in the instructionList
     */
    public int size() {
	return instructionList.size();
    }

    public double getRuntimeCost() {
	return runtimeCost;
    }

    public void setRuntimeCost(double runtimeCost) {
	this.runtimeCost = runtimeCost;
    }

    public void setInstructionList(List<Instruction> instructionList) {
	this.instructionList = instructionList;
    }

    public double getMappingSize() {
	return mappingSize;
    }

    public void setMappingSize(double mappingSize) {
	this.mappingSize = mappingSize;
    }

    public double getSelectivity() {
	return selectivity;
    }

    public void setSelectivity(double selectivity) {
	this.selectivity = selectivity;
    }

}
