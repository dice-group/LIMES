package org.aksw.limes.core.execution.planning.plan;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Implements the plan interface.
 *
 * @author ngonga
 * @author kleanthi
 */
public abstract class IPlan {
    public List<Instruction> instructionList;
    static Logger logger = Logger.getLogger("LIMES");

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
     * Checks whether a instructionList is empty
     * 
     */
    public abstract boolean isEmpty();

    /**
     * Returns the list of instructions contained in a instructionList
     * 
     * @return List of instructions
     */
    public abstract List<Instruction> getInstructionList();

    /**
     * Returns the size of a instructionList
     * 
     * @return Number of instructions in the instructionList
     */
    public abstract int size();

}
