package org.aksw.limes.core.execution.planning.plan;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Plan implements IPlan {
    static Logger logger = Logger.getLogger(Plan.class.getName());
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
            // instructions are added at the end of the list
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
     * Set the instructionList of the plan
     *
     * @param instructionList
     *         to set
     */
    public void setInstructionList(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    /**
     * Checks whether the instructionList of the current NestedPlan is empty
     *
     * @return true or false
     */
    public boolean isEmpty() {
        return instructionList.isEmpty();
    }

    @Override
    /**
     * Check if two plans are equal.
     *
     * @return true or false, if the plans have the same instruction list or not
     */
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

    /**
     * Returns the runtime cost of the plan
     *
     * @return runtimeCost
     */
    public double getRuntimeCost() {
        return runtimeCost;
    }

    /**
     * Set the runtime cost of the plan
     *
     * @param runtimeCost
     *         to set
     */
    public void setRuntimeCost(double runtimeCost) {
        this.runtimeCost = runtimeCost;
    }

    /**
     * Returns the mapping size of the plan
     *
     * @return mappingSize
     */
    public double getMappingSize() {
        return mappingSize;
    }

    /**
     * Set the mapping size of the plan
     *
     * @param mappingSize
     *         to set
     */
    public void setMappingSize(double mappingSize) {
        this.mappingSize = mappingSize;
    }

    /**
     * Returns the selectivity of the plan
     *
     * @return selectivity
     */
    public double getSelectivity() {
        return selectivity;
    }

    /**
     * Set the selectivity of the plan
     *
     * @param selectivity
     *         to set
     */
    public void setSelectivity(double selectivity) {
        this.selectivity = selectivity;
    }

}
