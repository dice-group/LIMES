package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.LigerPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.apache.log4j.Logger;

public abstract class PartialRecallRefinementOperator {
    protected static final Logger logger = Logger.getLogger(PartialRecallRefinementOperator.class.getName());

    protected PartialRecallRefinementNode root;
    protected PartialRecallRefinementNode best;
    protected double desiredSelectivity = 0.0d;
    protected ACache source;
    protected ACache target;
    protected long timeLimit;
    protected double k;
    protected long maxOpt;

    public double getRecall() {
        return k;
    }

    public long getOptimizationTime() {
        return maxOpt;
    }

    public PartialRecallRefinementNode getBest() {
        return best;
    }

    public double getDesiredSelectivity() {
        return desiredSelectivity;
    }

    protected List<Double> thresholds = Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

    protected LinkedList<PartialRecallRefinementNode> buffer = new LinkedList<PartialRecallRefinementNode>();
    protected HashSet<String> total = new HashSet<String>();

    public PartialRecallRefinementOperator(ACache s, ACache t, double recall, long optTime, LinkSpecification spec) {
        this.source = s;
        this.target = t;
        if (optTime < 0) {
            logger.info("\nOptimization time cannot be negative. Your input value is " + optTime
                    + ".\nSetting it to the default value: 0ms.");
            this.maxOpt = 0l;
        } else
            this.maxOpt = optTime;
        if (recall < 0.0 || recall > 1.0) {
            logger.info("\nExpected selectivity must be between 0.0 and 1.0. Your input value is " + k
                    + ".\nSetting it to the default value: 1.0.");
            this.k = 1.0d;
        } else
            this.k = recall;
        this.init(spec);
    }

    public abstract void optimize();

    /**
     * Compares an input selectivity value with the desired selectivity. If the
     * input selectivity is equal to the desired selectivity, the function
     * returns 0. If the input selectivity is lower than the desired
     * selectivity, it returns a value lower than 0. If the input selectivity is
     * larger than the desired selectivity, it returns a value larger than 0.
     * 
     * @param selectivity
     * @return
     */
    protected int checkSelectivity(double selectivity) {
        // selectivity = desiredSelectivity -> com = 0 - STOP
        // selectivity < desiredSelectivity -> com < 0 - STOP
        // selectivity > desiredSelectivity -> com > 0 - continue
        return Double.compare(selectivity, desiredSelectivity);

    }

    /**
     * Implements the next function. For an input threshold, it returns the
     * first larger threshold from a set of predefined thresholds. The returned
     * value can be at most 1.0. If the input threshold is already 1.0, it
     * returns a negative number that indicates that the link specification, to
     * which the input threshold belongs to, can not be refined any further.
     * 
     * @param currentThreshold,
     *            the input threshold
     * @return a value that is the first larger value than currentThreshold from
     *         a set of predefined values, or a negative number if the
     *         currentThreshold is already 1.0
     */
    protected double next(double currentThreshold) {
        if (currentThreshold < 0.0d || currentThreshold > 1.0)
            return -1.0d;

        if (Double.compare(currentThreshold, 1.0d) == 0) {
            return -1.0d;
        } else {
            for (double f : this.thresholds) {
                if (Double.compare(currentThreshold, f) < 0)
                    return f;
            }
        }
        return -1.0d;
    }

    /**
     * Initializes refinement procedure. It computes the canonical plan of the
     * input LS and based on its estimated selectivity, it computes the minimum
     * expected selectivity that a rapidly executable link specification
     * subsumed by L must achieve, based on the minimal expected recall
     * requirement set by the user. It also adds the input link specification to
     * the buffer and total structure. The buffer structure serves as a queue
     * and includes link specifications obtained by refining the initial
     * specifications, but have not yet been refined. All specifications, that
     * were generated through the refinement procedure as well as the input
     * specification, are stored in the total set. By keeping track of these
     * specifications, LIGER avoids refining a specification more than once and
     * address the redundancy of the refinement operator. This function also
     * initializes the best specification with the initial specification. The
     * best specification is updated via the main LIGER function, optimize().
     * The best specification is the subsumed specification with the lowest
     * runtime estimation, that abides to the minimal expected recall
     * requirement.
     * 
     * 
     * @param spec,
     *            the input link specification
     */
    protected void init(LinkSpecification spec) {

        if (spec == null) {
            logger.error("Link Specification is empty, I am going to stop here.");
            throw new RuntimeException();
        }

        LinkSpecification initLSClone = spec.clone();
        LigerPlanner planner = new LigerPlanner(this.source, this.target);
        Plan initPlan = planner.plan(initLSClone);
        this.best = new PartialRecallRefinementNode(initLSClone, initPlan);
        this.desiredSelectivity = (double) (initPlan.getSelectivity() * this.k);

        this.buffer.addFirst(best);
        this.total.add(best.getLinkSpecification().toString());

        // for safe keeping
        this.root = new PartialRecallRefinementNode(spec.clone(), initPlan);

    }

}
