package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureFactory;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions sequentially and returns a MemoryMemoryMapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public class SimpleExecutionEngine extends ExecutionEngine {

    private HashMap<String, Mapping> dynamicResults = new HashMap<String, Mapping>();

    /**
     * Implements running the run operator. Assume atomic measures
     *
     * @param inst
     *            Instruction
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @return MemoryMapping
     */
    public SimpleExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
        super(source, target, sourceVar, targetVar);
    }

    /**
     * Implementation of the execution of a plan. Be aware that this doesn't
     * executes complex Link Specifications.
     *
     * @param plan
     *            An execution plan
     * @return The mapping obtained from executing the plan
     */
    public Mapping executeInstructions(Plan plan) {
        buffer = new ArrayList<MemoryMapping>();
        if (plan.isEmpty()) {
            logger.info("Plan is empty. Done.");
            return new MemoryMapping();
        }
        List<Instruction> instructions = plan.getInstructionList();
        Mapping m = new MemoryMapping();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction inst = instructions.get(i);
            // get the index for writing the results
            int index = inst.getResultIndex();
            // first process the RUN operator
            if (inst.getCommand().equals(Command.RUN)) {
                m = executeRun(inst);
            } // runs the filter operator
            else if (inst.getCommand().equals(Command.FILTER)) {
                m = executeFilter(inst, buffer.get(inst.getSourceMapping()));
            } else if (inst.getCommand().equals(Command.REVERSEFILTER)) {
                m = executeReverseFilter(inst, buffer.get(inst.getSourceMapping()));
            }// runs set operations such as intersection,
            else if (inst.getCommand().equals(Command.INTERSECTION)) {
                m = executeIntersection(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
            } // union
            else if (inst.getCommand().equals(Command.UNION)) {
                m = executeUnion(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
            } // diff
            else if (inst.getCommand().equals(Command.DIFF)) {
                m = executeDifference(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
            } // xor
            else if (inst.getCommand().equals(Command.XOR)) {
                m = executeExclusiveOr(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
            } // end of processing. Return the indicated mapping
            else if (inst.getCommand().equals(Command.RETURN)) {
                logger.info("Reached return command. Returning results.");
                if (buffer.isEmpty()) {
                    return m;
                }
                if (index < 0) {// return last element of buffer
                    return buffer.get(buffer.size() - 1);
                } else {
                    return buffer.get(index);
                }
            }
            // place resulting mapping in the buffer
            if (index < 0) {// add the new mapping at the end of the list
                buffer.add((MemoryMapping) m);
            } else {
                // avoid overriding places in buffer
                // by adding the result at the end
                if (index < buffer.size()) {
                    buffer.add((MemoryMapping) m);
                } else {
                    // add placeholders to ensure that the mapping can be placed
                    // where the user wanted to have it
                    // new mappings are added at the end
                    while ((index + 1) > buffer.size()) {
                        buffer.add(new MemoryMapping());
                    }
                    buffer.set(index, (MemoryMapping) m);
                }

            }
        }

        // just in case the return operator was forgotten.
        // then we return the last mapping computed
        if (buffer.isEmpty()) {
            return new MemoryMapping();
        } else {
            return buffer.get(buffer.size() - 1);
        }
    }

    /**
     * Implements the execution of the run operator. Assume atomic measures.
     *
     * @param inst
     *            atomic run Instruction
     * @return The mapping obtained from executing the atomic run Instruction
     */
    public Mapping executeRun(Instruction inst) {
        double threshold = Double.parseDouble(inst.getThreshold());
        // generate correct mapper
        IMapper mapper;
        try {
            mapper = MeasureFactory.getMapper(inst.getMeasureExpression());
            return mapper.getMapping(source, target, sourceVariable, targetVariable, inst.getMeasureExpression(),
                    threshold);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new MemoryMapping();
    }
    /**
     * Runs the reverse filtering operator
     *
     * @param inst
     *            Instruction
     * @param input
     *            Mapping that is to be filtered
     * @return Filtered mapping
     */
    private Mapping executeReverseFilter(Instruction inst, Mapping input) {
        LinearFilter filter = new LinearFilter();
        return filter.reversefilter(input, inst.getMeasureExpression(), Double.parseDouble(inst.getThreshold()),
                Double.parseDouble(inst.getMainThreshold()), source, target, sourceVariable, targetVariable);
    }
    /**
     * Runs the filtering operator
     *
     * @param inst
     *            filter Instruction
     * @param input
     *            Mapping that is to be filtered
     * @return filtered Mapping
     */
    public Mapping executeFilter(Instruction inst, Mapping input) {
        LinearFilter filter = new LinearFilter();
        Mapping m = new MemoryMapping();
        if (inst.getMeasureExpression() == null)
            m = filter.filter(input, Double.parseDouble(inst.getThreshold()));
        else {
            if (inst.getMainThreshold() != null)
                m = filter.filter(input, inst.getMeasureExpression(), Double.parseDouble(inst.getThreshold()),
                        Double.parseDouble(inst.getMainThreshold()), source, target, sourceVariable, targetVariable);
            else// original filtering
                m = filter.filter(input, inst.getMeasureExpression(), Double.parseDouble(inst.getThreshold()), source,
                        target, sourceVariable, targetVariable);
        }

        return m;
    }

    /**
     * Implements the difference of Mappings
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeDifference(Mapping m1, Mapping m2) {
        return MappingOperations.difference(m1, m2);
    }

    /**
     * Implements the exclusive or of Mappings
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeExclusiveOr(Mapping m1, Mapping m2) {
        return MappingOperations.xor(m1, m2);
    }

    /**
     * Implements the intersection of Mappings
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeIntersection(Mapping m1, Mapping m2) {
        return MappingOperations.intersection(m1, m2);
    }

    /**
     * Implements the union of Mappings
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeUnion(Mapping m1, Mapping m2) {
        return MappingOperations.union(m1, m2);
    }

    /**
     * Implementation of the execution of a nested plan.
     *
     * @param plan,
     *            A nested plan
     * 
     * @return The mapping obtained from executing the plan
     */
    public Mapping executeStatic(NestedPlan plan) {
        // empty nested plan contains nothing
        Mapping m = new MemoryMapping();
        if (plan.isEmpty()) {
        } // atomic nested plan just contain simple list of instructions
        else if (plan.isAtomic()) {
            m = executeInstructions(plan);
        } // nested plans contain subplans, an operator for merging the results
        // of the subplans and a filter for filtering the results of the
        // subplan
        else {
            // run all the subplans
            m = executeStatic(plan.getSubPlans().get(0));
            Mapping m2, result = m;
            for (int i = 1; i < plan.getSubPlans().size(); i++) {
                m2 = executeStatic(plan.getSubPlans().get(i));
                if (plan.getOperator().equals(Command.INTERSECTION)) {
                    result = executeIntersection(m, m2);
                } // union
                else if (plan.getOperator().equals(Command.UNION)) {
                    result = executeUnion(m, m2);
                } // diff
                else if (plan.getOperator().equals(Command.DIFF)) {
                    result = executeDifference(m, m2);
                    // exclusive or
                } else if (plan.getOperator().equals(Command.XOR)) {
                    result = executeExclusiveOr(m, m2);
                }
                m = result;
            }
            // only run filtering if there is a filter indeed, else simply
            // return MemoryMapping
            if (plan.getFilteringInstruction() != null) {
                m = executeFilter(plan.getFilteringInstruction(), m);
            }
        }
        return m;
    }

    /**
     * If a plan is atomic: it gets run and the result mapping gets pushed on
     * the stack if its complex: the operator/filter is used on the top 2
     * objects on the stack which get popped the result mapping of this gets
     * pushed back on the stack
     * 
     * @param nestedPlan
     *            which has not yet been executed
     * @return Mapping
     */
    public Mapping executeDynamic(LinkSpecification spec, DynamicPlanner planner) {
        long begin = System.currentTimeMillis();
        long end = 0;
        Mapping m = new MemoryMapping();
        NestedPlan plan = new NestedPlan();
        // create function to check if linkspec has been seen before
        if (!planner.isExecuted(spec)) {
            String dependent = planner.getDependency(spec);
            if (dependent != null) {
                Mapping dependentM = dynamicResults.get(dependent);
                if (spec.getThreshold() > 0) {
                    // create a temporary filtering instruction
                    Instruction tempFilteringInstruction = new Instruction(Instruction.Command.FILTER, null,
                            spec.getThreshold() + "", -1, -1, 0);
                    m = executeFilter(tempFilteringInstruction, dependentM);
                }
            } else {
                if (spec.isEmpty()) {
                } else if (spec.isAtomic()) {
                    plan = planner.getPlan(spec);
                    if (plan.isEmpty()) // in case the init LS is atomic
                        plan = planner.plan(spec);
                    m = executeInstructions(plan);
                } else {
                    // complex not seen before
                    // call plan
                    plan = planner.plan(spec);
                    // get specification that corresponds to the first subplan
                    LinkSpecification firstSpec = planner.getLinkSpec(plan.getSubPlans().get(0));
                    // run first specification
                    m = executeDynamic(firstSpec, planner);
                    Mapping m2, result = m;
                    if (spec.getOperator().equals(LogicOperator.AND)) {
                        // replan
                        plan = planner.plan(spec);
                        // second plan is filter
                        if (plan.getOperator() == null) {
                            if (plan.getFilteringInstruction().getCommand().equals(Command.FILTER)) {
                                result = executeFilter(plan.getFilteringInstruction(), m);
                            }
                            // }
                        } else { // second plan is run
                            LinkSpecification secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                            m2 = executeDynamic(secondSpec, planner);
                            result = executeIntersection(m, m2);
                        }
                    } // union
                    else if (spec.getOperator().equals(LogicOperator.OR)) {
                        LinkSpecification secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                        if (secondSpec == null) {
                            plan = planner.plan(spec);
                            secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                        }
                        m2 = executeDynamic(secondSpec, planner);
                        result = executeUnion(m, m2);

                    } // diff
                    else if (spec.getOperator().equals(LogicOperator.MINUS)) {
                        // replan
                        plan = planner.plan(spec);
                        // second plan is (reverse) filter
                        if (plan.getOperator() == null) {
                            if (plan.getFilteringInstruction().getCommand().equals(Command.REVERSEFILTER)) {
                                result = executeFilter(plan.getFilteringInstruction(), m);

                            }
                        } else { // second plan is run
                            LinkSpecification secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                            m2 = executeDynamic(secondSpec, planner);
                            result = executeDifference(m, m2);

                        }
                    } else if (spec.getOperator().equals(LogicOperator.XOR)) {
                        LinkSpecification secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                        m2 = executeDynamic(secondSpec, planner);
                        result = executeExclusiveOr(m, m2);
                    }
                    m = result;
                    if (plan.getOperator() != null) {
                        if (plan.getFilteringInstruction() != null) {
                            m = executeFilter(plan.getFilteringInstruction(), m);
                        }
                    }

                }
            } // save results
            dynamicResults.put(spec.toString(), new MemoryMapping());
            dynamicResults.put(spec.toString(), m);
            end = System.currentTimeMillis();
            double msize = m.getNumberofMappings();
            double selectivity = msize / (source.size() * target.size());
            planner.updatePlan(spec, end - begin, selectivity, msize);
        } else {
            if (dynamicResults.containsKey(spec.toString())) {
                m = dynamicResults.get(spec.toString());
            } else {
                logger.info("Result for spec: " + spec + " not stored.Exiting..");
                System.exit(1);
            }
        }
        return m;
    }

    /**
     * Implementation of the execution of link specification. The execution
     * engine chooses which execute function is going to be invoked given the
     * planner. For the Canonical and Helios planners, the execution of link
     * specifications has been modeled as a linear process wherein a LS is
     * potentially first rewritten, planned and finally executed. Subsequently,
     * the plan never changes and simply executed. For the Dynamic planner, we
     * enable a flow of information from the execution engine back to the
     * planner, that uses intermediary execution results to improve plans
     * generated previously.
     *
     * @param spec,
     *            the link specification, after it was re-written
     * @param planner,
     *            the chosen planner
     * 
     * @return The mapping obtained from executing the plan of spec
     */
    @Override
    public Mapping execute(LinkSpecification spec, IPlanner planner) {
        Mapping m = new MemoryMapping();

        spec = planner.normalize(spec);
        if (planner.isStatic() == false) {
            m = executeDynamic(spec, (DynamicPlanner) planner);
        } else {
            NestedPlan plan = planner.plan(spec);
            m = executeStatic(plan);
        }

        return m;
    }

}
