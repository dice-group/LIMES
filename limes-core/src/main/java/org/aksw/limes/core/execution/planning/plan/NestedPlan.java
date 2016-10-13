package org.aksw.limes.core.execution.planning.plan;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.measures.measure.MeasureProcessor;

/**
 * Implements the nested plan of a link specification. Note that the subPlans
 * fields is set to null by the instructor. Before adding a subplan for the
 * first time, the subPlans field must be initiated.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class NestedPlan extends Plan {
    /**
     * List of nested sub-plans.
     */
    private List<NestedPlan> subPlans;
    /**
     * Operator of the nested plan.
     */
    private Command operator;
    /**
     * Filtering Instruction of the nested plan.
     */
    private Instruction filteringInstruction;
    /**
     * Fields that declares if the current nested plan is executed or not.
     */
    private boolean executionStatus = false;

    /**
     * Constructor of the NestedPlan class.
     */
    public NestedPlan() {
        super();
        subPlans = null;
        filteringInstruction = null;
    }

    /* Getters and setters */
    /**
     * Returns the set of sub-plans of the current plan.
     *
     * @return the current sub-plans
     */
    public List<NestedPlan> getSubPlans() {
        return subPlans;
    }

    /**
     * Sets the sub-plans of the plan.
     *
     * @param subPlans
     *            The sub-plans to set
     */
    public void setSubPlans(List<NestedPlan> subPlans) {
        this.subPlans = subPlans;
    }

    /**
     * Returns the operator of the plan.
     *
     * @return the operator of the plan
     */
    public Command getOperator() {
        return operator;
    }

    /**
     * Sets the operator of the plan.
     *
     * @param operator
     *            The operator to set
     */
    public void setOperator(Command operator) {
        this.operator = operator;
    }

    /**
     * Returns the filtering Instruction of the plan.
     *
     * @return the filtering instruction of the plan
     */
    public Instruction getFilteringInstruction() {
        return filteringInstruction;
    }

    /**
     * Sets the filtering Instruction of the plan.
     *
     * @param filteringInstruction
     *            The filtering instruction to set
     */
    public void setFilteringInstruction(Instruction filteringInstruction) {
        this.filteringInstruction = filteringInstruction;
    }

    /**
     * Returns the execution status of the plan.
     *
     * @return true if the current plan has been executed or false otherwise
     */
    public boolean getExecutionStatus() {
        return executionStatus;
    }

    /**
     * Updates the execution status of the plan. If the plan has just been
     * executed then it changes the value from false to true.
     *
     * @param executionStatus
     *            true if the plan has just been executed or false otherwise.
     */
    public void setExecutionStatus(boolean executionStatus) {
        this.executionStatus = executionStatus;
    }

    
    /**
     * Checks if the plan is empty. Returns true if and only if the instruction
     * list is empty and both the sub-plans and the filtering instructions are
     * null.
     *
     * @return true if the plan is empty and false otherwise
     */
     @Override
    public boolean isEmpty() {
        // instructionList is initiliazed as new list
        // subplans are null until a function initiliazes it
        // filteringInstruction is null until a function initiliazes it
        return (instructionList.isEmpty() == true && subPlans == null && filteringInstruction == null);
    }

    /**
     * Checks whether the current plan is atomic or not. A plan is atomic if its
     * sub-plans are null or if the existing sub-plans are empty.
     *
     * @return true if the plan is atomic and false otherwise
     */
    public boolean isAtomic() {
        if (subPlans == null) {
            return true;
        } else {
            if (subPlans.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the list of instructions contained in a instructionList. If the
     * plan is not atomic, then the functions returns all the instructions of
     * each atomic nested plan included in the current plan.
     *
     * @return a clone of the list of instructions of the current plan
     */
    @Override
    public List<Instruction> getInstructionList() {
        List<Instruction> instructions = new ArrayList<Instruction>();
        for (Instruction inst : instructionList) {
            instructions.add(inst);
        }
        if (!isAtomic()) {
            for (NestedPlan np : subPlans) {
                List<Instruction> instructions2 = np.getInstructionList();
                for (Instruction inst2 : instructions2) {
                    instructions.add(inst2);
                }
            }
        }
        return instructions;
    }

    /**
     * Returns the size of the current plan. The size of a plan is equal to the
     * size of its instruction list.
     *
     * @return the size of the plan
     */
    @Override
    public int size() {
        return (this.getInstructionList().size());
    }

    /**
     * Returns a clone of the current plan. Each non-primitive field of the
     * current plan is cloned by invoking the clone function of the
     * corresponding class.
     *
     * @return a clone of the current plan
     */
    @Override
    public NestedPlan clone() {
        NestedPlan clone = new NestedPlan();

        // clone primitives fields
        clone.setMappingSize(this.mappingSize);
        clone.setRuntimeCost(this.runtimeCost);
        clone.setSelectivity(this.selectivity);
        clone.setOperator(this.operator);

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

        // clone filteringInstruction
        if (this.filteringInstruction != null) {
            Instruction cloneFilteringInstruction = this.filteringInstruction.clone();
            clone.setFilteringInstruction(cloneFilteringInstruction);
        } else {
            clone.setFilteringInstruction(null);
        }

        // clone subplans
        if (this.subPlans != null) {
            if (this.subPlans.isEmpty() == false) {
                for (NestedPlan c : this.subPlans) {
                    NestedPlan subPlanCopy = c.clone();
                    clone.addSubplan(subPlanCopy);
                }
            } else
                clone.setSubPlans(new ArrayList<NestedPlan>());
        }
        return clone;
    }

    /**
     * Adds a sub-Plan to the current list of sub-plans. If there is no list one
     * will be created.
     *
     * @param subplan
     *            The sub-plan to be added
     */
    public void addSubplan(NestedPlan subplan) {
        if (subplan != null) {
            if (subPlans == null)
                setSubPlans(new ArrayList<NestedPlan>());
            subPlans.add(subplan);
        }
    }

    /**
     * Returns all the metric expressions of the current plan.
     *
     * @return List of all metric expressions
     */
    public List<String> getAllMeasures() {
        List<String> result = new ArrayList<String>();

        if (isAtomic()) {
            if (filteringInstruction != null) {
                result.addAll(MeasureProcessor.getMeasures(filteringInstruction.getMeasureExpression()));
            }
        }
        if (!(subPlans == null)) {
            if (!subPlans.isEmpty()) {
                for (NestedPlan p : subPlans) {
                    result.addAll(p.getAllMeasures());
                }
            }
        }
        if (instructionList != null) {
            for (Instruction i : instructionList) {
                if (i.getMeasureExpression() != null) {
                    result.addAll(MeasureProcessor.getMeasures(i.getMeasureExpression()));
                }
            }
        }
        return result;
    }

    /**
     * String representation of the current plan.
     *
     * @return a string representation of the current plan
     */
    public String toString() {
        String str = ("Selectivity = " + selectivity);
        if (isEmpty()) {
            return "Empty plan";
        }
        if (!instructionList.isEmpty()) {
            if (filteringInstruction != null) {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // instructionList, filteringInstruction, subplans
                        return "\nBEGIN\n" + str + "-----\n" + filteringInstruction + "\n-----\n" + instructionList
                                + "\n" + operator + "\nSubplans\n" + "\n" + subPlans + "\nEND\n-----";
                    } else
                        // instructionList, filteringInstruction
                        return "\nBEGIN\n" + str + "-----\n" + filteringInstruction + "\n-----\n" + instructionList
                                + "\nEND\n-----";
                } else {
                    // instructionList, filteringInstruction
                    return "\nBEGIN\n" + str + "-----\n" + filteringInstruction + "\n-----\n" + instructionList
                            + "\nEND\n-----";
                }
            } else {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // instructionList, subplans
                        return "\nBEGIN\n" + str + "-----\n" + instructionList + "\n" + operator + "\nSubplans\n"
                                + subPlans + "\nEND\n-----";
                    } else
                        // instructionList
                        return "\nBEGIN\n" + str + "-----\n" + instructionList + "\nEND\n-----";
                } else {
                    return "\nBEGIN\n" + str + "-----\n" + instructionList + "\nEND\n-----";
                }

            }
        } else {
            if (filteringInstruction != null) {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // filteringInstruction, subplans
                        return "\nBEGIN\n" + str + "-----\n" + filteringInstruction + "\n-----\n" + operator
                                + "\nSubplans\n" + "\n" + subPlans + "\nEND\n-----";
                    } else
                        // filteringInstruction
                        return "\nBEGIN\n" + str + "-----\n" + filteringInstruction + "\nEND\n-----";
                } else {
                    // filteringInstruction
                    return "\nBEGIN\n" + str + "-----\n" + filteringInstruction + "\nEND\n-----";
                }
            } else {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // subplans
                        return "\nBEGIN\n" + str + "-----\n" + operator + "\nSubplans\n" + "\n" + subPlans
                                + "\nEND\n-----";
                    }
                }
            }
        }
        return str;

    }

    /**
     * Returns the threshold of the current plan. If the filtering instruction
     * is not null, then it returns the threshold of the filtering instruction
     * and 0 otherwise.
     *
     * @return the threshold as string
     */
    public String getThreshold() {
        if (filteringInstruction != null) {
            return filteringInstruction.getThreshold();
        } else {
            return "0";
        }
    }

    /**
     * Returns a string representation of current plan as a set of commands.
     *
     * @return the current plan as a set of commands
     */
    public String finalPlan() {
        if (isEmpty()) {
            return "Empty plan";
        }
        if (isAtomic()) {
            return this.instructionList.get(0).getMeasureExpression() + "-"
                    + this.instructionList.get(0).getThreshold();
        } else {
            if (operator == null) {
                String child = subPlans.get(0).finalPlan();
                String filter = "";
                if (filteringInstruction.getCommand().equals(Instruction.Command.FILTER))
                    filter = "FILTER:" + filteringInstruction.getMeasureExpression() + "-"
                            + filteringInstruction.getThreshold();
                else
                    filter = "REVERSEFILTER:" + filteringInstruction.getMeasureExpression() + "-"
                            + filteringInstruction.getThreshold();
                String mainFilter = "FILTER:" + filteringInstruction.getMainThreshold();

                if (subPlans.get(0).isAtomic()) {
                    return "RUN:" + child + "\n" + filter + "\n" + mainFilter + "\n";
                } else
                    return child + "\n" + filter + "\n" + mainFilter + "\n";

            } else {
                String childLeft = subPlans.get(0).finalPlan();
                String childRight = subPlans.get(1).finalPlan();
                String op = "";
                if (this.operator.equals(Command.DIFF)) {
                    op = "DIFFERENCE";
                } else if (this.operator.equals(Command.INTERSECTION)) {
                    op = "INTERSECTION";
                } else if (this.operator.equals(Command.UNION)) {
                    op = "UNION";
                } else if (this.operator.equals(Command.XOR)) {
                    op = "XOR";
                }
                String filter = "FILTER:" + filteringInstruction.getThreshold();
                if (subPlans.get(0).isAtomic() && subPlans.get(1).isAtomic()) {
                    return "RUN:" + childLeft + "\n" + "RUN:" + childRight + "\n" + op + "\n" + filter + "\n";
                } else if (subPlans.get(0).isAtomic() && !subPlans.get(1).isAtomic()) {
                    return "RUN:" + childLeft + "\n" + childRight + "\n" + op + "\n" + filter + "\n";
                } else if (!subPlans.get(0).isAtomic() && subPlans.get(1).isAtomic()) {
                    return childLeft + "\n" + "RUN:" + childRight + "\n" + op + "\n" + filter + "\n";
                } else if (!subPlans.get(0).isAtomic() && !subPlans.get(1).isAtomic()) {
                    return childLeft + "\n" + childRight + "\n" + op + "\n" + filter + "\n";
                }
            }
        }
        return null;
    }

    /**
     * Compares the current plan with another plan, P. If P is null then it
     * returns false. If both plans are atomic, then the functions returns true
     * if they have the same instruction list. If both plans are complex, then
     * the function checks if each field of the current plan is equal to the
     * corresponding field of P. If one of the plans is atomic and the other is
     * not, then it returns false.
     *
     * @return true if both plans are equal, and false otherwise
     */
    @Override
    public boolean equals(Object other) {
        NestedPlan o = (NestedPlan) other;
        if (o == null)
            return false;

        // only RUN instructions in instructionList
        // no worries for null instructionList, constructor initializes it
        if (this.isAtomic() && o.isAtomic()) {
            return (this.instructionList.equals(o.instructionList));

        } else if (!this.isAtomic() && !o.isAtomic()) { // no instructionList
            if (this.operator == null && o.operator != null)
                return false;
            if (this.operator != null && o.operator == null)
                return false;
            // for optimized plans mostly
            if (this.operator == null && o.operator == null) {
                if (this.filteringInstruction == null && o.filteringInstruction == null) {
                    // instructionList must be empty for complex plans
                    // if it is not for any reason, the condition is still true
                    if (this.instructionList.equals(o.instructionList))
                        return (this.subPlans.equals(o.subPlans));
                    else
                        return false;
                }
                if (this.filteringInstruction != null && o.filteringInstruction == null)
                    return false;
                if (this.filteringInstruction == null && o.filteringInstruction != null)
                    return false;
                if (this.filteringInstruction.equals(o.filteringInstruction)) {
                    if (this.instructionList.equals(o.instructionList))
                        return (this.subPlans.equals(o.subPlans));
                    else
                        return false;
                }
                return false;
            }
            // for normal complex plans
            if (this.operator.equals(o.operator)) {
                // filtering instruction SHOULD not be null, but just in case
                if (this.filteringInstruction == null && o.filteringInstruction == null) {
                    if (this.instructionList.equals(o.instructionList))
                        return (this.subPlans.equals(o.subPlans));
                    else
                        return false;
                }
                if (this.filteringInstruction != null && o.filteringInstruction == null)
                    return false;
                if (this.filteringInstruction == null && o.filteringInstruction != null)
                    return false;
                if (this.filteringInstruction.equals(o.filteringInstruction)) {
                    if (this.instructionList.equals(o.instructionList))
                        return (this.subPlans.equals(o.subPlans));
                    else
                        return false;
                } // different filtering instructions
                return false;
            } // different operators
            return false;
        }
        // one plan is atomic, the other is not
        return false;

    }

}
