package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.ExtendedLinkSpecification;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.MapperFactory;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the Dynamic planner class. It receives a link specification as
 * input and generates a mutable NestedPlan.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class DynamicPlanner extends Planner {
    static Logger logger = LoggerFactory.getLogger(DynamicPlanner.class);
    /**
     * Source cache.
     */
    private ACache source;
    /**
     * Target cache.
     */
    private ACache target;
    /**
     * Language of the source/target data.
     */
    private Language lang;
    /**
     * Sub-link specifications (as string) and their corresponding plans.
     */
    private Map<String, NestedPlan> plans = new HashMap<String, NestedPlan>();
    /**
     * Sub-link specifications (as string) and their corresponding original
     * representation.
     */
    private Map<String, LinkSpecification> specifications = new HashMap<String, LinkSpecification>();
    /**
     * Sub-link specifications (as string) and their corresponding depended
     * specifications.
     */
    private Map<String, LinkSpecification> dependencies = new HashMap<String, LinkSpecification>();

    public DynamicPlanner(ACache s, ACache t) {
        source = s;
        target = t;
        lang = Language.EN;
    }

    public Map<String, NestedPlan> getPlans() {
        return plans;
    }

    /**
     * Initialize plans and specifications fields.
     *
     * @param spec
     *            The input link specification
     */
    public void init(LinkSpecification spec) {
        NestedPlan plan = new NestedPlan();
        if (!plans.containsKey(spec.toString())) {
            if (spec.isAtomic()) {
                plans.put(spec.toString(), plan);
                specifications.put(spec.toString(), spec);
            } else {
                for (LinkSpecification child : spec.getChildren()) {
                    init(child);
                }
                plans.put(spec.toString(), plan);
                specifications.put(spec.toString(), spec);
            }
        }
    }

    /**
     * Create/Update dependency between a recently executed specification and an
     * other specification. A specification L2 is dependent on an executed
     * specification L1 if L1 and L2 have the same metric expression and L1
     * threshold is lower that L2's threshold. Using this definition, L2 is a
     * sub-sumption of L1. Therefore, the execution of the initial specification
     * L is speeded-up. Instead of fully executing L2, dynamic planner informs
     * the execution engine about the dependency between L2 and L1, and the
     * execution engine retrieves the mapping of L1 from the results buffer and
     * creates a temporary filtering instruction in order to get L2's mapping
     * from L1's mapping. If L2 is dependent on L1 but it is already dependent
     * on another specification L3, then if L1's threshold must be higher than
     * L3' threshold in order to replace the previous L2-L3 dependency.
     *
     * @param spec
     *            The recently executed specification
     */
    public void createDependencies(LinkSpecification spec) {
        for (Entry<String, LinkSpecification> entry : specifications.entrySet()) {
            String dependentString = entry.getKey();
            LinkSpecification dependent = entry.getValue();
            if (spec.getFullExpression().equals(dependent.getFullExpression())
                    && spec.getThreshold() < dependent.getThreshold()) {
                if (dependencies.containsKey(dependentString)) {
                    LinkSpecification oldDependent = dependencies.get(dependentString);
                    if (oldDependent.getThreshold() < spec.getThreshold())
                        dependencies.put(dependentString, spec);
                } else
                    dependencies.put(dependentString, spec);
            }
        }
    }

    /**
     * Returns specification to whom the input specification parameter is
     * dependent upon, if any.
     *
     * @param spec
     *            The dependent specification
     * @return a string representation of specification that spec depends upon
     */
    public String getDependency(LinkSpecification spec) {
        String specString = spec.toString();
        if (dependencies.containsKey(specString)) {
            return dependencies.get(spec.toString()).toString();
        }
        return null;
    }

    /**
     * Computes atomic costs for a metric expression. If the metric expression
     * of is not supported by the framework, it throws an
     * InvalidMeasureException exception.
     *
     * @param measure
     *            Measure of metric expression
     * @param threshold
     *            Threshold of metric expression
     * @return runtime, estimated runtime cost of the metric expression
     * 
     */
    public double getAtomicRuntimeCosts(String measure, double threshold) {
        AMapper mapper = null;
        MeasureType type = MeasureFactory.getMeasureType(measure);
        mapper = MapperFactory.createMapper(type);
        return mapper.getRuntimeApproximation(source.size(), target.size(), threshold, lang);
    }

    /**
     * Computes atomic mapping sizes for a measure. If the metric expression of
     * is not supported by the framework, it throws an InvalidMeasureException
     * exception.
     *
     * @param measure
     *            Measure of metric expression
     * @param threshold
     *            Threshold of metric expression
     * @return estimated size of returned mapping
     * 
     */
    public double getAtomicMappingSizes(String measure, double threshold) {
        AMapper mapper = null;
        MeasureType type = MeasureFactory.getMeasureType(measure);
        mapper = MapperFactory.createMapper(type);
        return mapper.getMappingSizeApproximation(source.size(), target.size(), threshold, lang);
    }

    /**
     * Checks if the plan of the specified link specification is executed.
     *
     * @param spec
     *            The input link specification
     * @return true if the plan is executed, or false otherwise
     */
    public boolean isExecuted(LinkSpecification spec) {
        return (plans.get(spec.toString()).getExecutionStatus());
    }

    /**
     * Computes costs for a filtering instruction. If the metric expression of
     * the filtering instruction is not supported by the framework, it throws an
     * InvalidMeasureException exception.
     *
     * @param measures
     *            Set of expressions used to filter
     * @param mappingSize
     *            Size of mapping
     * @return estimated runtime cost of filteringInstruction(s)
     */
    public double getFilterCosts(List<String> measures, int mappingSize) {
        double cost = 0;
        if (measures != null) {
            for (String measure : measures) {
                double tempCost = 0;
                MeasureType type = MeasureFactory.getMeasureType(measure);
                tempCost = MeasureFactory.createMeasure(type).getRuntimeApproximation(mappingSize);
                cost += tempCost;
            }
        }
        return cost;
    }

    /**
     * Returns the corresponding plan of a specification.
     *
     * @param spec
     *            The link specification
     * @return the plan of the input specification
     */
    public NestedPlan getPlan(LinkSpecification spec) {
        if (plans.containsKey(spec.toString()))
            return plans.get(spec.toString());
        return null;
    }

    /**
     * Returns the specification from which the input plan was generated.
     *
     * @param plan
     *            The nested plan
     * @return the spec of the input plan
     */
    public LinkSpecification getLinkSpec(NestedPlan plan) {
        for (Map.Entry<String, NestedPlan> entry : plans.entrySet()) {
            String spec = entry.getKey();
            NestedPlan value = entry.getValue();
            if (value.equals(plan))
                return specifications.get(spec);
        }
        return null;
    }

    /**
     * Updates the characteristics of a plan once it is executed.
     *
     * @param spec
     *            The link specification from which the plan was generated
     * @param rt
     *            The real runtime of the plan
     * @param selectivity
     *            The real selectivity of the plan
     * @param msize
     *            The real mapping size returned when the plan is executed
     */
    public void updatePlan(LinkSpecification spec, double rt, double selectivity, double msize) {
        if (!plans.containsKey(spec.toString())) {
            logger.error("Specification: " + spec.getFullExpression() + " was not initialised. Exiting..");
            throw new RuntimeException();
        }
        NestedPlan plan = plans.get(spec.toString());
        plan.setRuntimeCost(rt);
        plan.setSelectivity(selectivity);
        plan.setMappingSize(msize);
        plan.setExecutionStatus(true);

        plans.put(spec.toString(), plan);
        createDependencies(spec);
    }

    /**
     * Generates a NestedPlan for a link specification.
     *
     * @param spec
     *            Input link specification
     * @return a NestedPlan of the input link specification
     */
    @Override
    public NestedPlan plan(LinkSpecification spec) {
        return plan(spec, source, target, MappingFactory.createDefaultMapping(), MappingFactory.createDefaultMapping());

    }

    /**
     * Generates a instructionList based on the optimality assumption used in
     * databases. If the input link specification has an AND or a MINUS
     * operator, then the plan function will find the least costly plan from a
     * set of alternatives (see {@link #getBestConjunctivePlan},
     * {@link #getBestDifferencePlan} resp.).
     *
     * @param spec
     *            Input link specification
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceMapping
     *            Size of source mapping
     * @param targetMapping
     *            Size of target mapping
     * @return a NestedPlan for the input link specification
     */
    public NestedPlan plan(LinkSpecification spec, ACache source, ACache target, AMapping sourceMapping,
            AMapping targetMapping) {
        NestedPlan plan = new NestedPlan();

        // if plan is executed, just return the plan
        // remember that the plan is automatically updated once it is executed
        plan = plans.get(spec.toString());
        if (plan.getExecutionStatus()) {
            return plan;
        }
        plan = new NestedPlan();
        // atomic specs are simply ran
        if (spec.isAtomic()) {
            Parser p = new Parser(spec.getFilterExpression(), spec.getThreshold());
            plan.setInstructionList(new ArrayList<Instruction>());
            plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            plan.setRuntimeCost(getAtomicRuntimeCosts(p.getOperator(), spec.getThreshold()));
            plan.setMappingSize(getAtomicMappingSizes(p.getOperator(), spec.getThreshold()));
            plan.setSelectivity(plan.getMappingSize() / (double) (source.size() * target.size()));

        } else {
            if (spec.getOperator().equals(LogicOperator.OR)) {
                List<NestedPlan> children = new ArrayList<NestedPlan>();
                double runtimeCost = 0;
                for (LinkSpecification child : spec.getChildren()) {
                    NestedPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
                    children.add(childPlan);
                    runtimeCost = runtimeCost + childPlan.getRuntimeCost();
                }
                // RUNTIME
                plan.setRuntimeCost(runtimeCost + (spec.getChildren().size() - 1));
                // SUBPLANS
                plan.setSubPlans(children);
                // FILTERING INSTRUCTION
                plan.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                        spec.getThreshold() + "", -1, -1, 0));
                // OPERATOR
                plan.setOperator(Instruction.Command.UNION);
                // SELECTIVITY
                double selectivity = 1 - children.get(0).getSelectivity();
                for (int i = 1; i < children.size(); i++) {
                    selectivity = selectivity * (1 - children.get(i).getSelectivity());
                }
                plan.setSelectivity(1 - selectivity);
                // MAPPING SIZE
                plan.setMappingSize(source.size() * target.size() * plan.getSelectivity());
                // add filtering costs based on approximation of mapping
                // size
                if (plan.getFilteringInstruction().getMeasureExpression() != null) {
                    plan.setRuntimeCost(plan.getRuntimeCost()
                            + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
                                    source.size() * target.size() * plan.getSelectivity()));
                }
            } else if (spec.getOperator().equals(LogicOperator.XOR)) {
                List<NestedPlan> children = new ArrayList<NestedPlan>();
                double runtimeCost = 0;
                for (LinkSpecification child : spec.getChildren()) {
                    NestedPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
                    children.add(childPlan);
                    runtimeCost = runtimeCost + childPlan.getRuntimeCost();
                }
                // RUNTIME
                plan.setRuntimeCost(runtimeCost + (spec.getChildren().size() - 1));
                // SUBPLANS
                plan.setSubPlans(children);
                // FILTERING INSTRUCTION
                plan.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                        spec.getThreshold() + "", -1, -1, 0));
                // OPERATOR
                plan.setOperator(Instruction.Command.XOR);
                // SELECTIVITY
                // A XOR B = (A U B) \ (A & B)
                double selectivity = children.get(0).getSelectivity();
                for (int i = 1; i < children.size(); i++) {
                    selectivity = (1 - (1 - selectivity) * (1 - children.get(i).getSelectivity()))
                            * (1 - selectivity * children.get(i).getSelectivity());
                }
                plan.setSelectivity(selectivity);
                // MAPPING SIZE
                plan.setMappingSize(source.size() * target.size() * plan.getSelectivity());
                // add filtering costs based on approximation of mapping
                // size
                if (plan.getFilteringInstruction().getMeasureExpression() != null) {
                    plan.setRuntimeCost(plan.getRuntimeCost()
                            + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
                                    source.size() * target.size() * plan.getSelectivity()));
                }

            } else if (spec.getOperator().equals(LogicOperator.MINUS)) {
                List<NestedPlan> children = new ArrayList<NestedPlan>();
                plan.setRuntimeCost(0);
                for (LinkSpecification child : spec.getChildren()) {
                    NestedPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
                    children.add(childPlan);
                }
                // SELECTIVITY
                double selectivity = children.get(0).getSelectivity();
                for (int i = 1; i < children.size(); i++) {
                    // selectivity is not influenced by bestConjuctivePlan
                    selectivity = selectivity * (1 - children.get(i).getSelectivity());
                }
                plan = getBestDifferencePlan(spec, children.get(0), children.get(1), selectivity);

            } else if (spec.getOperator().equals(LogicOperator.AND)) {
                List<NestedPlan> children = new ArrayList<NestedPlan>();
                plan.setRuntimeCost(0);
                for (LinkSpecification child : spec.getChildren()) {
                    NestedPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
                    children.add(childPlan);
                }
                // SELECTIVITY
                double selectivity = 1d;
                for (int i = 0; i < children.size(); i++) {
                    // selectivity is not influenced by bestConjuctivePlan
                    selectivity = selectivity * children.get(i).getSelectivity();
                }
                // this puts all options to this.steps and returns the best plan
                plan = getBestConjunctivePlan(spec, children.get(0), children.get(1), selectivity);
            }
        }
        this.plans.put(spec.toString(), plan);
        // logger.info(spec+"");
        // logger.info(plan.getRuntimeCost()+"");
        // logger.info(plan+"");
        // logger.info("--------------------------------------------------------------------");
        return plan;

    }

    /**
     * Find the least costly plan for a link specification with MINUS operator.
     * Computes all possible nested plans given the children plans. If none of
     * the children plans are executed or only the left child plan is executed,
     * then the functions chooses either to (1) Execute both children plans,
     * perform difference and filter the resulting mapping using the threshold
     * of the link specification, or (2) Execute the plan of the left child, use
     * the right child measure expression as a filer and then filter the
     * resulting mapping using the threshold of the link specification. If the
     * right child plan is executed, then the least costly plan is (1). The
     * selection of the best alternative is based upon runtime estimations
     * obtained for each of the atomic measure expressions included in the
     * children specifications.
     * 
     * @param spec
     *            The link specification
     * @param left
     *            Left child nested plan
     * @param right
     *            Right child nested plan
     * @param selectivity
     *            The overall selectivity
     * @return the resulting nested plan for the input spec, that is least
     *         costly
     */
    public NestedPlan getBestDifferencePlan(LinkSpecification spec, NestedPlan left, NestedPlan right,
            double selectivity) {
        double runtime1 = 0, runtime2 = 0;
        NestedPlan result = new NestedPlan();
        double mappingSize = source.size() * target.size() * selectivity;

        // both children are executed: do DIFF
        if (left.getExecutionStatus() && right.getExecutionStatus()) {
            // OPERATOR
            result.setOperator(Instruction.Command.DIFF);
            // SUBPLANS
            List<NestedPlan> plans = new ArrayList<NestedPlan>();
            plans.add(left);
            plans.add(right);
            result.setSubPlans(plans);
            // FILTERING INSTRUCTION
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            // RUNTIME
            // result.runtimeCost = left.runtimeCost + right.runtimeCost;
            result.setRuntimeCost(0.0d);
            // SELECTIVITY
            result.setSelectivity(selectivity);
            // MAPPING SIZE
            result.setMappingSize(mappingSize);
            return result;
        } // if right child is executed, then there is one option: run left and
          // then do filter
        else if (!left.getExecutionStatus() && right.getExecutionStatus()) {
            // OPERATOR
            result.setOperator(Instruction.Command.DIFF);
            // FILTERING INSTRUCTION
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            // SUBPLANS
            List<NestedPlan> plans = new ArrayList<NestedPlan>();
            plans.add(left);
            plans.add(right);
            result.setSubPlans(plans);
            // RUNTIME
            // seems like a good idea ..
            // result.runtimeCost = left.runtimeCost + right.runtimeCost;
            result.setRuntimeCost(left.getRuntimeCost());
            // SELECTIVITY
            result.setSelectivity(selectivity);
            // MAPPING SIZE
            result.setMappingSize(mappingSize);
            return result;
        }
        // if left is/isn't executed and right is not executed: run right, DIFF
        // OR REVERSEFILTER with right
        // never add the runtime of left if it is already executed
        // first instructionList: run both children and then merge

        if (!left.getExecutionStatus())
            runtime1 = left.getRuntimeCost();

        runtime1 = runtime1 + right.getRuntimeCost();

        result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                spec.getThreshold() + "", -1, -1, 0));
        if (result.getFilteringInstruction().getMeasureExpression() != null) {
            runtime1 = runtime1 + MeasureProcessor.getCosts(result.getFilteringInstruction().getMeasureExpression(),
                    (int) Math.ceil(source.size() * target.size() * selectivity));
        }
        ////////////////////////////////////////////////////////////////////////
        // second instructionList: run left child and use right child as filter
        if (!left.getExecutionStatus())
            runtime2 = left.getRuntimeCost();
        runtime2 = runtime2 + getFilterCosts(right.getAllMeasures(),
                (int) Math.ceil(source.size() * target.size() * right.getSelectivity()));

        double min = Math.min(runtime1, runtime2);
        if (min == runtime1) {
            result.setOperator(Instruction.Command.DIFF);
            List<NestedPlan> plans = new ArrayList<NestedPlan>();
            plans.add(left);
            plans.add(right);
            result.setSubPlans(plans);
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
        } else if (min == runtime2) {
            String rightChild = spec.getChildren().get(1).getFullExpression();
            result.setFilteringInstruction(new Instruction(Instruction.Command.REVERSEFILTER, rightChild,
                    spec.getChildren().get(1).getThreshold() + "", -1, -1, 0));
            result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
            result.setOperator(null);
            List<NestedPlan> plans = new ArrayList<NestedPlan>();
            plans.add(left);
            result.setSubPlans(plans);
        }
        result.setRuntimeCost(min);
        result.setSelectivity(selectivity);
        result.setMappingSize(mappingSize);
        return result;
    }

    /**
     * Find the least costly plan for a link specification with AND operator.
     * Computes all possible nested plans given the children plans. If none of
     * the children plans are executed then the functions chooses either to (1)
     * Execute both children plans, perform intersection and filter the
     * resulting mapping using the threshold of the link specification, or (2)
     * Execute the plan of the left child, use the right child measure
     * expression as a filer and then filter the resulting mapping using the
     * threshold of the link specification, or (3) Execute the plan of the right
     * child, use the left child measure expression as a filer and then filter
     * the resulting mapping using the threshold of the link specification. If
     * the left child plan is executed, then the least costly plan is either (1)
     * or (2). If the right child plan is executed, then the least costly plan
     * is either (1) or (3). If both children plans are executed then the least
     * costly plan is (1). The selection of the best alternative is based upon
     * runtime estimations obtained for each of the atomic measure expressions
     * included in the children specifications.
     * 
     * @param spec
     *            The link specification
     * @param left
     *            Left child nested plan
     * @param right
     *            Right child nested plan
     * @param selectivity
     *            The overall selectivity
     * @return the resulting nested plan for the input spec, that is least
     *         costly
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, NestedPlan left, NestedPlan right,
            double selectivity) {
        double runtime1 = 0, runtime2 = 0, runtime3 = 0;
        NestedPlan result = new NestedPlan();

        // both children are executed: do AND
        if (left.getExecutionStatus() && right.getExecutionStatus()) {
            // OPERATOR
            result.setOperator(Instruction.Command.INTERSECTION);
            // SUBPLANS
            List<NestedPlan> plans = new ArrayList<NestedPlan>();
            plans.add(left);
            plans.add(right);
            result.setSubPlans(plans);
            // FILTERING INSTRUCTION
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            // RUNTIME
            // result.runtimeCost = left.runtimeCost + right.runtimeCost;
            result.setRuntimeCost(0.0d);
            // SELECTIVITY
            result.setSelectivity(selectivity);
            // MAPPING SIZE
            result.setMappingSize(source.size() * target.size() * selectivity);
            return result;
        } // left is executed, right is not: RUN B, FILTER OR FILTER WITH B
        else if (left.getExecutionStatus() && !right.getExecutionStatus()) {
            // first instructionList: run both children and then merge
            runtime1 = right.getRuntimeCost();
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            if (result.getFilteringInstruction().getMeasureExpression() != null) {
                runtime1 = runtime1 + MeasureProcessor.getCosts(result.getFilteringInstruction().getMeasureExpression(),
                        (int) Math.ceil(source.size() * target.size() * selectivity));
            }
            // second instructionList: run left child and use right child as
            // filter
            // RUNTIME
            runtime2 = getFilterCosts(right.getAllMeasures(),
                    (int) Math.ceil(source.size() * target.size() * right.getSelectivity()));

            double min = Math.min(runtime1, runtime2);
            if (min == runtime1) {
                result.setOperator(Instruction.Command.INTERSECTION);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(left);
                plans.add(right);
                result.setSubPlans(plans);
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                        spec.getThreshold() + "", -1, -1, 0));
            } else {
                String rightChild = spec.getChildren().get(1).getFullExpression();
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, rightChild,
                        spec.getChildren().get(1).getThreshold() + "", -1, -1, 0));
                result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
                result.setOperator(null);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(left);
                result.setSubPlans(plans);
            }
            result.setRuntimeCost(min);
            result.setSelectivity(selectivity);
            result.setMappingSize(source.size() * target.size() * selectivity);
            return result;

        } // left is not executed: RUN A, FILTER OR FILTER WITH A
        else if (!left.getExecutionStatus() && right.getExecutionStatus()) {
            // first instructionList: run both children and then merge
            // runtime1 = left.runtimeCost + right.runtimeCost;
            runtime1 = left.getRuntimeCost();
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            if (result.getFilteringInstruction().getMeasureExpression() != null) {
                runtime1 = runtime1 + MeasureProcessor.getCosts(result.getFilteringInstruction().getMeasureExpression(),
                        (int) Math.ceil(source.size() * target.size() * selectivity));
            }
            // third instructionList: run right child and use left child as
            // runtime3 = right.runtimeCost;
            runtime3 = getFilterCosts(left.getAllMeasures(),
                    (int) Math.ceil(source.size() * target.size() * left.getSelectivity()));

            double min = Math.min(runtime1, runtime3);
            if (min == runtime1) {
                result.setOperator(Instruction.Command.INTERSECTION);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(right);
                plans.add(left);
                result.setSubPlans(plans);
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                        spec.getThreshold() + "", -1, -1, 0));

            } else // min == runtime3
            {
                String leftChild = spec.getChildren().get(0).getFullExpression();
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, leftChild,
                        spec.getChildren().get(0).getThreshold() + "", -1, -1, 0));
                result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
                result.setOperator(null);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(right);
                result.setSubPlans(plans);
            }
            result.setRuntimeCost(min);
            result.setSelectivity(selectivity);
            result.setMappingSize(source.size() * target.size() * selectivity);
            return result;

        } // if either of the children is executed, then 3 options available
        else if (!left.getExecutionStatus() && !right.getExecutionStatus()) {
            // first instructionList: run both children and then merge
            runtime1 = left.getRuntimeCost() + right.getRuntimeCost();
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            if (result.getFilteringInstruction().getMeasureExpression() != null) {
                runtime1 = runtime1 + MeasureProcessor.getCosts(result.getFilteringInstruction().getMeasureExpression(),
                        (int) Math.ceil(source.size() * target.size() * selectivity));
            }
            // second instructionList: run left child and use right child as
            // filter
            runtime2 = left.getRuntimeCost();
            runtime2 = runtime2 + getFilterCosts(right.getAllMeasures(),
                    (int) Math.ceil(source.size() * target.size() * right.getSelectivity()));

            // third instructionList: run right child and use left child as
            // filter
            runtime3 = right.getRuntimeCost();
            runtime3 = runtime3 + getFilterCosts(left.getAllMeasures(),
                    (int) Math.ceil(source.size() * target.size() * left.getSelectivity()));

            double min = Math.min(Math.min(runtime3, runtime2), runtime1);
            if (min == runtime1) {
                result.setOperator(Instruction.Command.INTERSECTION);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(left);
                plans.add(right);
                result.setSubPlans(plans);
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                        spec.getThreshold() + "", -1, -1, 0));

            } else if (min == runtime2) {
                String rightChild = spec.getChildren().get(1).getFullExpression();
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, rightChild,
                        spec.getChildren().get(1).getThreshold() + "", -1, -1, 0));
                result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
                result.setOperator(null);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(left);
                result.setSubPlans(plans);

            } else // min == runtime3
            {
                String leftChild = spec.getChildren().get(0).getFullExpression();
                result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, leftChild,
                        spec.getChildren().get(0).getThreshold() + "", -1, -1, 0));
                result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
                result.setOperator(null);
                List<NestedPlan> plans = new ArrayList<NestedPlan>();
                plans.add(right);
                result.setSubPlans(plans);
            }
            result.setRuntimeCost(min);
            result.setSelectivity(selectivity);
            result.setMappingSize(source.size() * target.size() * selectivity);
            return result;
        }

        return result;
    }

    /**
     * Returns the status of the planner.
     *
     * @return false
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * Normalization of input link specification. In case of XOR operator, the
     * output specification uses the extended form of XOR (i.e.
     * XOR(cosine(x.name,y.name)|0.5, overlap(x.label,y.label)|0.6){@literal >}
     * =0.8 will transformed into MINUS(OR(cosine(x.name,y.name)|0.5,
     * overlap(x.label,y.label)|0.6)|0.8, AND(cosine(x.name,y.name)|0.5,
     * overlap(x.label,y.label)|0.6)|0.8) ){@literal >}=0.8
     *
     * @param spec
     *            The normalized link specification
     */
    @Override
    public LinkSpecification normalize(LinkSpecification spec) {
        if (spec.isEmpty()) {
            return spec;
        }
        LinkSpecification ls = new ExtendedLinkSpecification(spec.getFullExpression(), spec.getThreshold());
        init(ls);
        return ls;
    }

}
