package org.aksw.limes.core.execution.planning.plan;

import java.util.List;

public interface IPlan {
    /**
     * Adds an instruction to the instructionList
     * 
     * @param instruction
     *            Instruction
     */
    public abstract void addInstruction(Instruction instruction);

    /**
     * Removes the ith instruction from the instructionList
     * 
     * @param i
     *            Index of instruction to remove
     */
    public abstract void removeInstruction(int i);

    /**
     * Removes an instruction from a instructionList
     * 
     * @param i
     *            Instruction to remove
     */
    public abstract void removeInstruction(Instruction i);

    

    /**
     * Returns the list of instructions contained in a instructionList
     * 
     * @return List of instructions
     */
    public abstract List<Instruction> getInstructionList();
    
    
}
