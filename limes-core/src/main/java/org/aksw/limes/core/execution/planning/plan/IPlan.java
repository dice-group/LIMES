/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.execution.planning.plan;

import org.aksw.limes.core.util.Clonable;

import java.util.List;

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
