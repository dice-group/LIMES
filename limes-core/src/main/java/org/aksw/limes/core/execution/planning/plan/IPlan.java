package org.aksw.limes.core.execution.planning.plan;

import java.util.List;

import org.aksw.limes.core.util.Clonable;

/**
 * Implements the Plan Interface. 
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IPlan extends Clonable<IPlan> {
    /**
     * Adds an instruction to the instructionList.
     *
     * @param instruction
     *         The Instruction to add
     */
    public abstract void addInstruction(Instruction instruction);

    /**
     * Removes the i-th instruction from the instructionList.
     *
     * @param index
     *         Index of instruction to remove
     */
    public abstract void removeInstruction(int index);

    /**
     * Removes san instruction from a instructionList.
     *
     * @param instruction
     *         Instruction to remove
     */
    public abstract void removeInstruction(Instruction instruction);


    /**
     * Returns the list of instructions contained in a instructionList.
     *
     * @return List of instructions
     */
    public abstract List<Instruction> getInstructionList();


}
