package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;

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
 * Implements the Helios planner class. It receives a link specification as
 * input and generates an immutable NestedPlan.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class HeliosPlanner extends Planner {

    static Logger logger = LoggerFactory.getLogger(HeliosPlanner.class);
    /**
     * Source cache.
     */
    public ACache source;
    /**
     * Target cache.
     */
    public ACache target;
    /**
     * Language of the source/target data.
     */
    public Language lang;

    /**
     * Constructor of the Helios planner class.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target get
     */
    public HeliosPlanner(ACache source, ACache target) {
        this.source = source;
        this.target = target;
        this.lang = Language.EN;
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
     * @return estimated runtime cost of the metric expression
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
     */
    public double getAtomicMappingSizes(String measure, double threshold) {
        AMapper mapper = null;
        MeasureType type = MeasureFactory.getMeasureType(measure);
        mapper = MapperFactory.createMapper(type);
        return mapper.getMappingSizeApproximation(source.size(), target.size(), threshold, lang);
    }

    /**
     * Computes costs for a filtering instruction. If the metric expression of
     * the filtering instruction is not supported by the framework, it throws an
     * InvalidMeasureException exception.
     *
     * @param measures
     *            The set of expressions used to filter
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
     * Generates a NestedPlan for a link specification.
     *
     * @param spec
     *            Input link specification
     * @return the corresponding NestedPlan of the input link specification
     */
    @Override
    public NestedPlan plan(LinkSpecification spec) {
        return plan(spec, source, target, MappingFactory.createDefaultMapping(), MappingFactory.createDefaultMapping());
    }

    /**
     * Generates a NestedPlan based on the optimality assumption used in
     * databases. If the input link specification has an AND operator, then the
     * plan function will find the least costly plan from a set of alternatives
     * (see {@link #getBestConjunctivePlan} }.
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
        // atomic specs are simply ran
        if (spec == null)
            return plan;
        if (spec.isEmpty())
            return plan;
        if (spec.isAtomic()) {
            // here we should actually choose between different implementations
            // of the operators based on their runtimeCost
            Parser p = new Parser(spec.getFilterExpression(), spec.getThreshold());
            plan.setInstructionList(new ArrayList<Instruction>());
            plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            plan.setRuntimeCost(getAtomicRuntimeCosts(p.getOperator(), spec.getThreshold()));
            plan.setMappingSize(getAtomicMappingSizes(p.getOperator(), spec.getThreshold()));
            plan.setSelectivity(plan.getMappingSize() / (double) (source.size() * target.size()));
        } else {
            // no optimization for non AND operators really
            if (!spec.getOperator().equals(LogicOperator.AND)) {
                List<NestedPlan> children = new ArrayList<NestedPlan>();
                // set children and update costs
                plan.setRuntimeCost(0);
                for (LinkSpecification child : spec.getChildren()) {
                    NestedPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
                    children.add(childPlan);
                    plan.setRuntimeCost(plan.getRuntimeCost() + childPlan.getRuntimeCost());
                }
                // add costs of union, which are 1
                plan.setRuntimeCost(plan.getRuntimeCost() + (spec.getChildren().size() - 1));
                plan.setSubPlans(children);
                plan.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                        spec.getThreshold() + "", -1, -1, 0));
                // set operator
                double selectivity;
                if (spec.getOperator().equals(LogicOperator.OR)) {
                    plan.setOperator(Instruction.Command.UNION);
                    selectivity = 1 - children.get(0).getSelectivity();
                    for (int i = 1; i < children.size(); i++) {
                        selectivity = selectivity * (1 - children.get(i).getSelectivity());
                    }
                    plan.setSelectivity(1 - selectivity);
                } else if (spec.getOperator().equals(LogicOperator.MINUS)) {
                    plan.setOperator(Instruction.Command.DIFF);
                    // p(A \ B \ C \ ... ) = p(A) \ p(B U C U ...)
                    selectivity = children.get(0).getSelectivity();
                    for (int i = 1; i < children.size(); i++) {
                        selectivity = selectivity * (1 - children.get(i).getSelectivity());

                    }
                    plan.setSelectivity(selectivity);
                } else if (spec.getOperator().equals(LogicOperator.XOR)) {
                    plan.setOperator(Instruction.Command.XOR);
                    // A XOR B = (A U B) \ (A & B)
                    selectivity = children.get(0).getSelectivity();
                    for (int i = 1; i < children.size(); i++) {
                        selectivity = (1 - (1 - selectivity) * (1 - children.get(i).getSelectivity()))
                                * (1 - selectivity * children.get(i).getSelectivity());
                    }
                    plan.setSelectivity(selectivity);
                }
                // add filtering costs based on approximation of mapping
                // size
                if (plan.getFilteringInstruction().getMeasureExpression() != null) {
                    plan.setRuntimeCost(plan.getRuntimeCost()
                            + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
                                    source.size() * target.size() * plan.getSelectivity()));
                }
            } // here we can optimize.
            else if (spec.getOperator().equals(LogicOperator.AND)) {
                List<NestedPlan> children = new ArrayList<NestedPlan>();
                plan.setRuntimeCost(0);
                double selectivity = 1d;
                for (LinkSpecification child : spec.getChildren()) {
                    NestedPlan childPlan = plan(child);
                    children.add(childPlan);
                    plan.setRuntimeCost(plan.getRuntimeCost() + childPlan.getRuntimeCost());
                    selectivity = selectivity * childPlan.getSelectivity();
                }
                plan = getBestConjunctivePlan(spec, children, selectivity);
            }
        }
        return plan;
    }

    /**
     * Computes the left-order best instructionList for a list of plans. Only
     * needed when AND has more than 2 children. Simply splits the task in
     * computing the best instructionList for (leftmost, all others).
     *
     * @param spec
     *            Input link specification
     * @param plans
     *            List of plans
     * @param selectivity
     *            Selectivity of the instructionList (known beforehand)
     * @return a NestedPlan of the input link specification
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, List<NestedPlan> plans, double selectivity) {
        if (plans == null) {
            return null;
        }
        if (plans.isEmpty()) {
            return new NestedPlan();
        }
        if (plans.size() == 1) {
            return plans.get(0);
        }
        if (plans.size() == 2) {
            return getBestConjunctivePlan(spec, plans.get(0), plans.get(1), selectivity);
        } else {
            NestedPlan left = plans.get(0);
            plans.remove(plans.get(0));
            return getBestConjunctivePlan(spec, left, plans, selectivity);
        }
    }

    /**
     * Computes the best conjunctive instructionList for a instructionList
     * against a list of plans by calling back the method.
     *
     * @param spec
     *            Input link specification
     * @param left
     *            Left instructionList
     * @param plans
     *            List of other plans
     * @param selectivity
     *            Overall selectivity
     * @return a NestedPlan of the input link specification
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, NestedPlan left, List<NestedPlan> plans,
            double selectivity) {
        if (plans == null) {
            return left;
        }
        if (plans.isEmpty()) {
            return left;
        }
        if (plans.size() == 1) {
            return getBestConjunctivePlan(spec, left, plans.get(0), selectivity);
        } else {
            NestedPlan right = getBestConjunctivePlan(spec, plans, selectivity);
            return getBestConjunctivePlan(spec, left, right, selectivity);
        }
    }

    /**
     * Find the least costly plan for a link specification with AND operator.
     * Computes all possible nested plans given the children plans: (1) Execute
     * both children plans, perform intersection and filter the resulting
     * mapping using the threshold of the link specification. (2) Execute the
     * plan of the left child, use the right child measure expression as a filer
     * and then filter the resulting mapping using the threshold of the link
     * specification. (3) Execute the plan of the right child, use the left
     * child measure expression as a filer and then filter the resulting mapping
     * using the threshold of the link specification. The selection of the best
     * alternative is based upon runtime estimations obtained for each of the
     * atomic measure expressions included in the children specifications.
     *
     * @param spec
     *            The link specification
     * @param left
     *            Left child nested plan
     * @param right
     *            Right child nested plan
     * @param selectivity,
     *            The overall selectivity
     * @return the resulting nested plan for the input spec, that is least
     *         costly
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, NestedPlan left, NestedPlan right,
            double selectivity) {
        double runtime1 = 0, runtime2, runtime3;
        NestedPlan result = new NestedPlan();
        // first instructionList: run both children and then merge
        runtime1 = left.getRuntimeCost() + right.getRuntimeCost();
        result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                spec.getThreshold() + "", -1, -1, 0));
        if (result.getFilteringInstruction().getMeasureExpression() != null) {
            runtime1 = runtime1 + MeasureProcessor.getCosts(result.getFilteringInstruction().getMeasureExpression(),
                    (int) Math.ceil(source.size() * target.size() * selectivity));
        }
        // second instructionList: run left child and use right child as filter
        runtime2 = left.getRuntimeCost();
        runtime2 = runtime2 + getFilterCosts(right.getAllMeasures(),
                (int) Math.ceil(source.size() * target.size() * right.getSelectivity()));
        // third instructionList: run right child and use left child as filter
        runtime3 = right.getRuntimeCost();
        runtime3 = runtime3 + getFilterCosts(left.getAllMeasures(),
                (int) Math.ceil(source.size() * target.size() * left.getSelectivity()));

        double min = Math.min(Math.min(runtime3, runtime2), runtime1);

        if (min == runtime1) {
            result.setOperator(Instruction.Command.INTERSECTION);
            List<NestedPlan> subplans = new ArrayList<NestedPlan>();
            subplans.add(left);
            subplans.add(right);
            result.setSubPlans(subplans);
        } else if (min == runtime2) {
            String rightChild = spec.getChildren().get(1).getFullExpression();
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, rightChild,
                    spec.getChildren().get(1).getThreshold() + "", -1, -1, 0));
            result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
            result.setOperator(null);
            List<NestedPlan> subplans = new ArrayList<NestedPlan>();
            subplans.add(left);
            result.setSubPlans(subplans);
        } else // min == runtime3
        {
            String leftChild = spec.getChildren().get(0).getFullExpression();
            result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, leftChild,
                    spec.getChildren().get(0).getThreshold() + "", -1, -1, 0));
            result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
            result.setOperator(null);
            List<NestedPlan> subplans = new ArrayList<NestedPlan>();
            subplans.add(right);
            result.setSubPlans(subplans);
        }
        result.setRuntimeCost(min);
        result.setSelectivity(selectivity);
        result.setMappingSize(source.size() * target.size() * selectivity);
        return result;
    }

    /**
     * Returns the status of the planner.
     *
     * @return true
     */
    @Override
    public boolean isStatic() {
        return true;
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
        return ls;
    }
}
