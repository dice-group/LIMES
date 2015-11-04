package org.aksw.limes.core.execution.planning.plan;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * Implements the plan interface.
 *
 * @author ngonga
 * @author kleanthi
 */
public abstract class Plan {
    public List<Instruction> instructionList;
    static Logger logger = Logger.getLogger("LIMES");

    
    /** Adds an instruction to the instructionList
     * 
     * @param instruction Instruction
     */
    public void addInstruction(Instruction instruction)
    {
//    	System.out.println("Add Instruction to list"+instructionList+" the inst:"+instruction);
    	boolean added = instructionList.add(instruction);
    	if(!added)
    		logger.info("ExecutionPlan.addInstructiun() failed");
    }
    
    /** Removes the ith instruction from the instructionList
     * 
     * @param i Index of instruction to remove
     */
    public void removeInstruction(int i)
    {
        instructionList.remove(i);
    }
    
    /** Removes an instruction from a instructionList
     * 
     * @param i Instruction to remove
     */
    public void removeInstruction(Instruction i)
    {
        instructionList.remove(i);
    }
    
    /** Checks whether a instructionList is empty
     * 
     */
    public boolean isEmpty()
    {
        return instructionList.isEmpty();
    }
    
    /** Returns the list of instructions contained in a instructionList
     * 
     * @return List of instructions
     */
    public List<Instruction> getInstructionList()
    {
        return instructionList;
    }
    
    /** Returns the size of a instructionList
     * 
     * @return Number of instructions in the instructionList
     */
    public int size()
    {
        return instructionList.size();
    }

}
