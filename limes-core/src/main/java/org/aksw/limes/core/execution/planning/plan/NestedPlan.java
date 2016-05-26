package org.aksw.limes.core.execution.planning.plan;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.measures.measure.MeasureProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements execution plan that is given to an execution engine. Note that the
 * subPlans fields is set to null by the instructor. Before adding a subplan for
 * the first time, the subPlans field must be initiated.
 *
 * @author ngonga
 * @author kleanthi
 */
public class NestedPlan extends Plan {
    private List<NestedPlan> subPlans;
    private Command operator;
    private Instruction filteringInstruction;
    private boolean isExecuted;

    public NestedPlan() {
        super();
        subPlans = null;
        filteringInstruction = null;
    }

    /**
     * Returns the set of sub-Plans of the plan
     *
     * @return subPlans
     */
    public List<NestedPlan> getSubPlans() {
        return subPlans;
    }

    /**
     * Set the sub-Plans of the plan
     *
     * @param subPlans
     *         to set
     */
    public void setSubPlans(List<NestedPlan> subPlans) {
        this.subPlans = subPlans;
    }

    /**
     * Returns the operator of the plan
     *
     * @return operator
     */
    public Command getOperator() {
        return operator;
    }

    /**
     * Set the operator of the plan
     *
     * @param operator
     *         to set
     */
    public void setOperator(Command operator) {
        this.operator = operator;
    }

    /**
     * Returns the filtering Instruction of the plan
     *
     * @return filteringInstruction
     */
    public Instruction getFilteringInstruction() {
        return filteringInstruction;
    }

    /**
     * Set the filtering Instruction of the plan
     *
     * @param filteringInstruction
     *         to set
     */
    public void setFilteringInstruction(Instruction filteringInstruction) {
        this.filteringInstruction = filteringInstruction;
    }

    @Override
    public boolean isEmpty() {
        // instructionList is initiliazed as new list
        // subplans are null until a function initiliazes it
        // filteringInstruction is null until a function initiliazes it
        return (instructionList.isEmpty() == true && subPlans == null && filteringInstruction == null);
    }

    /**
     * Checks whether the current NestedPlan is atomic or not.
     *
     * @return true, if current NestedPlan is atomic. false, if otherwise
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

    @Override
    public List<Instruction> getInstructionList() {
        List<Instruction> instructions = new ArrayList<Instruction>();
        for (Instruction inst : instructionList) {
            instructions.add(inst.clone());
        }
        if (!isAtomic()) {
            for (NestedPlan np : subPlans) {
                List<Instruction> instructions2 = np.getInstructionList();
                for (Instruction inst2 : instructions2) {
                    instructions.add(inst2.clone());
                }
            }
        }
        return instructions;
    }

    @Override
    public int size() {
        return (this.getInstructionList().size());
    }

    /**
     * Generates a clone of the current NestedPlan
     *
     * @return clone, clone of current NestedPlan
     */
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
     * Adds a sub-Plan to the current list of subPlans, if there is no list one
     * will be created
     *
     * @param subplan
     *         to be added
     */
    public void addSubplan(NestedPlan subplan) {
        if (subplan != null) {
            if (subPlans == null)
                setSubPlans(new ArrayList<NestedPlan>());
            subPlans.add(subplan);
        }
    }

    /**
     * Get all the metric expressions of the current NestedPlan
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
     * String representation of NestedPlan
     *
     * @return NestedPlan as string
     */
    public String toString() {
        String pre = ("Selectivity = " + selectivity);
        if (isEmpty()) {
            return "Empty plan";
        }
        if (!instructionList.isEmpty()) {
            if (filteringInstruction != null) {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // instructionList, filteringInstruction, subplans
                        return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\n-----\n" + instructionList
                                + "\n" + operator + "\nSubplans\n" + "\n" + subPlans + "\nEND\n-----";
                    } else
                        // instructionList, filteringInstruction
                        return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\n-----\n" + instructionList
                                + "\nEND\n-----";
                } else {
                    // instructionList, filteringInstruction
                    return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\n-----\n" + instructionList
                            + "\nEND\n-----";
                }
            } else {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // instructionList, subplans
                        return "\nBEGIN\n" + pre + "-----\n" + instructionList + "\n" + operator + "\nSubplans\n"
                                + subPlans + "\nEND\n-----";
                    } else
                        // instructionList
                        return "\nBEGIN\n" + pre + "-----\n" + instructionList + "\nEND\n-----";
                } else {
                    return "\nBEGIN\n" + pre + "-----\n" + instructionList + "\nEND\n-----";
                }

            }
        } else {
            if (filteringInstruction != null) {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // filteringInstruction, subplans
                        return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\n-----\n" + operator
                                + "\nSubplans\n" + "\n" + subPlans + "\nEND\n-----";
                    } else
                        // filteringInstruction
                        return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\nEND\n-----";
                } else {
                    // filteringInstruction
                    return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\nEND\n-----";
                }
            } else {
                if (subPlans != null) {
                    if (!subPlans.isEmpty()) {
                        // subplans
                        return "\nBEGIN\n" + pre + "-----\n" + operator + "\nSubplans\n" + "\n" + subPlans
                                + "\nEND\n-----";
                    }
                }
            }
        }
        return pre;

    }

    /**
     * Returns the threshold to be used when reconstructing the metric that led
     * to the current NestedPlan
     *
     * @return Threshold as string
     */
    public String getThreshold() {
        if (filteringInstruction != null) {
            return filteringInstruction.getThreshold();
        } else {
            return "0";
        }
    }

    /**
     * String representation of NestedPlan as a set of commands
     *
     * @return NestedPlan as a set of commands
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

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean isExecuted) {
        this.isExecuted = isExecuted;
    }

}
