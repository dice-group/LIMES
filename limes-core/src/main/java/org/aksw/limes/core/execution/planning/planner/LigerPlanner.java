package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.MapperFactory;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a planner class used by the partial recall execution engine class.
 * It receives a link specification as input and generates an immutable
 * NestedPlan. This planner has the same functionality as the Canonical planner,
 * but updates information regarding the runtime and selectivity of the
 * generated plan of the link specification and its sub-specifications. It is
 * only to be used by the partial recall execution engine class during the
 * production of the best subsumed link specification. Instances of the
 * LigerPlanner can not and should not be created via a configuration file or
 * the EngineFactory.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class LigerPlanner extends Planner {
    static Logger logger = LoggerFactory.getLogger(LigerPlanner.class);

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
    public LigerPlanner(ACache source, ACache target) {
        this.source = source;
        this.target = target;
        this.lang = Language.EN;
    }

    /**
     * Generates a NestedPlan for a link specification. If the link
     * specification is null or empty it returns an empty plan. If the link
     * specification is atomic, the planner generates a simple plan that
     * consists of one RUN instruction. If the link specification is complex,
     * firstly it generates a plan for each of the children specifications and
     * sets them as subPlans. Then, it assigns to the plan the corresponding
     * command based on the link specification operator and finally creates a
     * filtering instruction using the filtering expression and threshold of the
     * input link specification.
     *
     * @param spec
     *            Input link specification
     * @return a NestedPlan of the input link specification
     */
    @Override
    public NestedPlan plan(LinkSpecification spec) {

        NestedPlan plan = new NestedPlan();
        // atomic specs are simply ran
        if (spec == null)
            return plan;
        if (spec.isEmpty())
            return plan;
        if (spec.isAtomic()) {
            Parser p = new Parser(spec.getFilterExpression(), spec.getThreshold());
            plan.setInstructionList(new ArrayList<Instruction>());
            // nested plan have a null instruction list as default
            plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            ////////////////////////////////
            MeasureType type = MeasureFactory.getMeasureType(p.getOperator());
            AMapper mapper = MapperFactory.createMapper(type);

            double runtimeEstimation = mapper.getRuntimeApproximation(source.size(), target.size(), spec.getThreshold(),
                    Language.EN);
            double sizeEstimation = mapper.getMappingSizeApproximation(source.size(), target.size(),
                    spec.getThreshold(), Language.EN);

            plan.setRuntimeCost(runtimeEstimation);
            plan.setMappingSize(sizeEstimation);
            plan.setSelectivity(sizeEstimation / ((double) source.size() * target.size()));
            //logger.info("\n atomic sel: " + plan.getSelectivity());
            //logger.info("\n atomic runtime: " + plan.getRuntimeCost());
            ////////////////////////////////
            plan.setSelectivity(plan.getMappingSize() / (double) (source.size() * target.size()));
        } else {
            List<NestedPlan> children = new ArrayList<NestedPlan>();
            plan.setRuntimeCost(0);
            // set children
            for (LinkSpecification child : spec.getChildren()) {
                NestedPlan childPlan = plan(child);
                children.add(childPlan);
                plan.setRuntimeCost(plan.getRuntimeCost() + childPlan.getRuntimeCost());
            }
            // operators have constant time 1.0
            plan.setRuntimeCost(plan.getRuntimeCost() + (spec.getChildren().size() - 1));
            plan.setSubPlans(children);

            plan.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
            if (plan.getFilteringInstruction().getMeasureExpression() != null) {
                plan.setRuntimeCost(plan.getRuntimeCost()
                        + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
                                source.size() * target.size() * plan.getSelectivity()));
            }

            double selectivity;
            // set operator
            if (spec.getOperator().equals(LogicOperator.AND)) {
                plan.setOperator(Instruction.Command.INTERSECTION);
                selectivity = 1d;
                for (int i = 0; i < children.size(); i++) {
                    selectivity = selectivity * children.get(i).getSelectivity();
                }
                plan.setSelectivity(0.5 * selectivity);

            } else if (spec.getOperator().equals(LogicOperator.OR)) {
                plan.setOperator(Instruction.Command.UNION);
                selectivity = 1 - children.get(0).getSelectivity();
                for (int i = 1; i < children.size(); i++) {
                    selectivity = selectivity * (1 - children.get(i).getSelectivity());
                }
                plan.setSelectivity(0.5 * (1 - selectivity));

            } else if (spec.getOperator().equals(LogicOperator.MINUS)) {
                plan.setOperator(Instruction.Command.DIFF);
                selectivity = children.get(0).getSelectivity();
                for (int i = 0; i < children.size(); i++) {
                    selectivity = selectivity * (1 - children.get(i).getSelectivity());

                }
                plan.setSelectivity(0.5 * selectivity);

            }

        }
        return plan;

    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public LinkSpecification normalize(LinkSpecification spec) {
        return spec;
    }

}
