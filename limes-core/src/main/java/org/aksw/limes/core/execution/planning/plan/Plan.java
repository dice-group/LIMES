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
    protected List<NestedPlan> subPlans;
    protected Command operator;
    protected Instruction filteringInstruction;

    public Plan() {
	setInstructionList(new ArrayList<Instruction>());
	setRuntimeCost(0d);
	setMappingSize(0d);
	setSelectivity(1d);
	setSubPlans(null);
	setFilteringInstruction(null);
    }

    @Override
    public void addInstruction(Instruction instruction) {
	if (instruction != null) {
	    boolean added = getInstructionList().add(instruction);
	    if (!added)
		logger.info("ExecutionPlan.addInstructiun() failed");
	}

    }

    @Override
    public void removeInstruction(int i) {
	if (i >= getInstructionList().size() || i < 0)
	    logger.info("ExecutionPlan.removeInstructiun() failed");
	else
	    getInstructionList().remove(i);
    }

    @Override
    public void removeInstruction(Instruction i) {
	getInstructionList().remove(i);
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
	return getInstructionList().isEmpty();
    }
    /**
     * Checks whether the current NestedPlan is atomic
     * 
     * @return true, if current NestedPlan is atomic. false, if otherwise
     */
    public boolean isAtomic() {
	if (getSubPlans() == null) {
	    return true;
	} else {
	    if (getSubPlans().isEmpty()) {
		return true;
	    }
	}
	return false;
    }
    /**
     * Returns the size of the instructionList of the current NestedPlan
     * 
     * @return Number of instructions in the instructionList
     */
    public int size() {
	return getInstructionList().size();
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

    public List<NestedPlan> getSubPlans() {
	return subPlans;
    }

    public void setSubPlans(List<NestedPlan> subPlans) {
	this.subPlans = subPlans;
    }

    public Command getOperator() {
	return operator;
    }

    public void setOperator(Command operator) {
	this.operator = operator;
    }

    public Instruction getFilteringInstruction() {
	return filteringInstruction;
    }

    public void setFilteringInstruction(Instruction filteringInstruction) {
	this.filteringInstruction = filteringInstruction;
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
