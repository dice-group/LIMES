package org.aksw.limes.core.execution.planning.plan;

import org.aksw.limes.core.util.Clonable;

import java.util.List;

/**
 * Implements the Plan Interface. 
 *
 * @author Kleanthi Georgala <georgala@informatik.uni-leipzig.de>
 * @version 1.0
 */
public interface IPlan extends Clonable<IPlan> {
    /**
     * Adds an instruction to the instructionList.
     *
     * @param instruction,
     *         the Instruction to add
     */
    public abstract void addInstruction(Instruction instruction);

    /**
     * Removes the i-th instruction from the instructionList.
     *
     * @param i,
     *         Index of instruction to remove
     */
    public abstract void removeInstruction(int i);

    /**
     * Removes an instruction from a instructionList.
     *
     * @param i
     *         Instruction to remove
     */
    public abstract void removeInstruction(Instruction i);


    /**
     * Returns the list of instructions contained in a instructionList.
     *
     * @return List of instructions
     */
    public abstract List<Instruction> getInstructionList();


}
