package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.MapperFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets as input a link specification and a planner type, executes the
 * independent parts of the plan returned from the planner sequentially and
 * returns a MemoryMemoryMapping.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class SimpleExecutionEngine extends ExecutionEngine {

    static Logger logger = LoggerFactory.getLogger(SimpleExecutionEngine.class);
    /**
     * Map of intermediate mappings. Used for dynamic planning.
     */
    private HashMap<String, AMapping> dynamicResults = new HashMap<String, AMapping>();

    /**
     * Constructor for a simple execution engine.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Source variable
     * @param targetVar
     *            Target variable
     */
    public SimpleExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar) {
        super(source, target, sourceVar, targetVar);
    }

    /**
     * Constructor for a simple execution engine.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Source variable
     * @param targetVar
     *            Target variable
     * @param maxOpt,
     *            optimization time constraint
     * @param k,
     *            expected selectivity
     */
    public SimpleExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar, long maxOpt,
            double k) {
        super(source, target, sourceVar, targetVar, maxOpt, k);
    }

    /**
     * Implementation of the execution of a plan. It receives a plan as a set of
     * instructions and executes them sequentially. This function does not
     * execute nested plans. In case of a RUN command, the instruction must
     * include an atomic link specification.
     *
     * @param plan
     *            An execution plan
     * @return The mapping obtained from executing the plan
     */
    public AMapping executeInstructions(Plan plan) {
        setBuffer(new ArrayList<>());
        if (plan.isEmpty()) {
            logger.info("Plan is empty. Done.");
            return MappingFactory.createDefaultMapping();
        }
        List<Instruction> instructions = plan.getInstructionList();
        AMapping m = MappingFactory.createDefaultMapping();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction inst = instructions.get(i);
            // get the index for writing the results
            int index = inst.getResultIndex();
            // first process the RUN operator
            if (inst.getCommand().equals(Command.RUN)) {
                m = executeRun(inst);
            } // runs the filter operator
            else if (inst.getCommand().equals(Command.FILTER)) {
                m = executeFilter(inst, getBuffer().get(inst.getSourceIndex()));
            } else if (inst.getCommand().equals(Command.REVERSEFILTER)) {
                m = executeReverseFilter(inst, getBuffer().get(inst.getSourceIndex()));
            } // runs set operations such as intersection,
            else if (inst.getCommand().equals(Command.INTERSECTION)) {
                m = executeIntersection(getBuffer().get(inst.getSourceIndex()), getBuffer().get(inst.getTargetIndex()));
            } // union
            else if (inst.getCommand().equals(Command.UNION)) {
                m = executeUnion(getBuffer().get(inst.getSourceIndex()), getBuffer().get(inst.getTargetIndex()));
            } // diff
            else if (inst.getCommand().equals(Command.DIFF)) {
                m = executeDifference(getBuffer().get(inst.getSourceIndex()), getBuffer().get(inst.getTargetIndex()));
            } // xor
            else if (inst.getCommand().equals(Command.XOR)) {
                LinearFilter f = new LinearFilter();
                AMapping m1 = executeUnion(getBuffer().get(inst.getSourceIndex()), getBuffer().get(inst.getTargetIndex()));
                m1 = f.filter(m1, Double.parseDouble(inst.getThreshold()));
                AMapping m2 = executeIntersection(getBuffer().get(inst.getSourceIndex()), getBuffer().get(inst.getTargetIndex()));
                m2 = f.filter(m2, Double.parseDouble(inst.getThreshold()));
                m = executeDifference(m1, m2);
            } // end of processing. Return the indicated mapping
            else if (inst.getCommand().equals(Command.RETURN)) {
                if (getBuffer().isEmpty()) {
                    return m;
                }
                if (index < 0) {// return last element of buffer
                    return getBuffer().get(getBuffer().size() - 1);
                } else {
                    return getBuffer().get(index);
                }
            }
            // place resulting mapping in the buffer
            if (index < 0) {// add the new mapping at the end of the list
                getBuffer().add((MemoryMapping) m);
            } else {
                // avoid overriding places in buffer
                // by adding the result at the end
                if (index < getBuffer().size()) {
                    getBuffer().add((MemoryMapping) m);
                } else {
                    // add placeholders to ensure that the mapping can be placed
                    // where the user wanted to have it
                    // new mappings are added at the end
                    while ((index + 1) > getBuffer().size()) {
                        getBuffer().add(MappingFactory.createDefaultMapping());
                    }
                    getBuffer().set(index, (MemoryMapping) m);
                }

            }
        }

        // just in case the return operator was forgotten.
        // then we return the last mapping computed
        if (getBuffer().isEmpty()) {
            return MappingFactory.createDefaultMapping();
        } else {
            return getBuffer().get(getBuffer().size() - 1);
        }
    }

    /**
     * Implements the execution of the RUN operator. The input instruction must
     * include an atomic link specification.
     *
     * @param inst
     *            Atomic RUN instruction
     * @return The mapping obtained from executing the atomic RUN instruction
     */
    public AMapping executeRun(Instruction inst) {
        double threshold = Double.parseDouble(inst.getThreshold());
        // try {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);

        } else {
            IMapper mapper;
            // try {
            MeasureType type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);

            return mapper.getMapping(source, target, sourceVariable, targetVariable, inst.getMeasureExpression(),
                    threshold);
            /*
             * } catch (InvalidMeasureException e) { e.printStackTrace();
             * logger.info("Returning an empty mapping"); }
             */
        }
        /*
         * } catch (InvalidThresholdException e) { e.printStackTrace();
         * logger.info("Returning an empty mapping"); }
         */

    }

    /**
     * Runs the reverse filtering operator.
     *
     * @param inst
     *            Input instruction with REVERSEFILTER command
     * @param input
     *            Mapping that is to be filtered
     * @return Filtered mapping
     */
    private AMapping executeReverseFilter(Instruction inst, AMapping input) {
        LinearFilter filter = new LinearFilter();
        return filter.reversefilter(input, inst.getMeasureExpression(), Double.parseDouble(inst.getThreshold()),
                Double.parseDouble(inst.getMainThreshold()), source, target, sourceVariable, targetVariable);
    }

    /**
     * Runs the filtering operator.
     *
     * @param inst
     *            Input instruction with FILTER command
     * @param input
     *            Mapping that is to be filtered
     * @return filtered Mapping
     */
    public AMapping executeFilter(Instruction inst, AMapping input) {
        LinearFilter filter = new LinearFilter();
        AMapping m = MappingFactory.createDefaultMapping();
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
     * Implements the difference between two mappings.
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Difference of m1 and m2
     */
    public AMapping executeDifference(AMapping m1, AMapping m2) {
        return MappingOperations.difference(m1, m2);
    }

    /**
     * Implements the intersection between two mappings.
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Intersection of m1 and m2
     */
    public AMapping executeIntersection(AMapping m1, AMapping m2) {
        return MappingOperations.intersection(m1, m2);
    }

    /**
     * Implements the union between two mappings.
     *
     * @param m1
     *            First Mapping
     * @param m2
     *            Second Mapping
     * @return Intersection of m1 and m2
     */
    public AMapping executeUnion(AMapping m1, AMapping m2) {
        return MappingOperations.union(m1, m2);
    }

    /**
     * Executes an immutable nested plan in lNr depth first oder. See
     * {@link #execute(LinkSpecification, IPlanner)}. If a plan is atomic, it is
     * executed and the result mapping is returned. If it is complex, the
     * operator is used on the top 2 previously retrieved mappings and the
     * result mapping of the previous step gets filtered using the filtering
     * instruction of the plan (if any).
     *
     * @param plan
     *            A nested plan created by a static planner (Canonical or
     *            Helios)
     * @return The mapping obtained from executing the plan
     */
    public AMapping executeStatic(NestedPlan plan) {
        // empty nested plan contains nothing
        AMapping m = MappingFactory.createDefaultMapping();
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
            AMapping m2, result = m;
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
                    LinearFilter f = new LinearFilter();
                    AMapping mleft = executeUnion(m, m2);
                    mleft = f.filter(mleft, Double.parseDouble(plan.getThreshold()));

                    AMapping mright = executeIntersection(m, m2);
                    mright = f.filter(mright, Double.parseDouble(plan.getThreshold()));
                    result = executeDifference(mleft, mright);
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
     * Executes an input link specification L in a dynamic fashion. See
     * {@link #execute(LinkSpecification, IPlanner)}. If L is already executed,
     * then the corresponding mapping is retrieved from the dynamicResults and
     * returned. If L is not executed, then the function checks if there is
     * another previously executed specification A with the same fullExpression
     * ( {@link org.aksw.limes.core.io.ls.LinkSpecification#fullExpression} but
     * lower threshold. If A exists, then it retrieves the mapping of A, filters
     * it with input link specification threshold and returns the mapping. If A
     * does not exist, the function checks whether L is atomic or not. If L is
     * atomic then the planner creates a nested plan P with a RUN instruction.
     * The function executes P and the retrieved mapping is returned. If L is
     * not atomic, then the planner creates an initial plan P for L and the
     * function executes the first subPlan of P, P1. Then, if L has an OR or XOR
     * operator, the second subPlan of P, P2 is executed, the operator functions
     * is applied to the mappings returned by P1 and P2 and the resulting
     * mapping is filtered and returned. In case of an AND or MINUS operator,
     * the executeDynamic function invokes the planning function again to
     * re-plan the remaining non-executed parts of P (if any). Then it executes
     * the second sub-plan of P, that can vary given from the initial given the
     * intermediate executed steps of the first plan.
     * 
     *
     * @param spec
     *            The input link specification
     * @param planner,
     *            The dynamic planner
     * @return The mapping obtained from executing the link specification.
     */
    public AMapping executeDynamic(LinkSpecification spec, DynamicPlanner planner) {
        long begin = System.currentTimeMillis();
        long end = 0;
        AMapping m = MappingFactory.createDefaultMapping();
        NestedPlan plan = new NestedPlan();
        // create function to check if linkspec has been seen before
        if (!planner.isExecuted(spec)) {
            String dependent = planner.getDependency(spec);
            if (dependent != null) {
                AMapping dependentM = dynamicResults.get(dependent);
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
                    AMapping m2, result = m;
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
                                result = executeReverseFilter(plan.getFilteringInstruction(), m);

                            }
                        } else { // second plan is run
                            LinkSpecification secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                            m2 = executeDynamic(secondSpec, planner);
                            result = executeDifference(m, m2);

                        }
                    } else if (spec.getOperator().equals(LogicOperator.XOR)) {
                        LinkSpecification secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                        if (secondSpec == null) {
                            plan = planner.plan(spec);
                            secondSpec = planner.getLinkSpec(plan.getSubPlans().get(1));
                        }
                        m2 = executeDynamic(secondSpec, planner);
                        LinearFilter f = new LinearFilter();
                        AMapping mleft = executeUnion(m, m2);
                        mleft = f.filter(mleft, Double.parseDouble(plan.getThreshold()));

                        AMapping mright = executeIntersection(m, m2);
                        mright = f.filter(mright, Double.parseDouble(plan.getThreshold()));
                        result = executeDifference(mleft, mright);
                    }
                    m = result;
                    if (plan.getOperator() != null) {
                        if (plan.getFilteringInstruction() != null) {
                            m = executeFilter(plan.getFilteringInstruction(), m);
                        }
                    }

                }
            } // save results
            dynamicResults.put(spec.toString(), MappingFactory.createDefaultMapping());
            dynamicResults.put(spec.toString(), m);
            end = System.currentTimeMillis();
            double msize = m.getNumberofMappings();
            double selectivity = msize / (source.size() * target.size());
            planner.updatePlan(spec, end - begin, selectivity, msize);
        } else {
            if (dynamicResults.containsKey(spec.toString())) {
                m = dynamicResults.get(spec.toString());
            } else {
                logger.info("Error in spec: " + spec + ". Result not stored.");
                throw new RuntimeException();
            }
        }

        return m;
    }

    /**
     * Executes a link specification. The execution engine chooses which execute
     * function is going to be invoked given the planner. For the Canonical and
     * Helios planner, the execution of link specifications has been modeled as
     * a linear process wherein a LS is potentially first rewritten, planned and
     * finally executed. Subsequently, the plan never changes and is simply
     * executed. For the Dynamic planner, we enable a flow of information from
     * the execution engine back to the planner, that uses intermediary
     * execution results to improve plans generated previously.
     *
     * @param spec
     *            The link specification, after it was re-written
     * @param planner
     *            The chosen planner
     * @return The mapping obtained from executing the plan of the input link
     *         specification
     */
    @Override
    public AMapping execute(LinkSpecification spec, IPlanner planner) {
        AMapping m = MappingFactory.createDefaultMapping();

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
