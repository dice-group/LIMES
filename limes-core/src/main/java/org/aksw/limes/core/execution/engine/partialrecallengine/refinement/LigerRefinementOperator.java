package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.LigerPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.apache.log4j.Logger;

public class LigerRefinementOperator extends PartialRecallRefinementOperator {

    private HashMap<LinkSpecification, Plan> newNodes = new HashMap<LinkSpecification, Plan>();

    public HashMap<LinkSpecification, Plan> getNewNodes() {
        return newNodes;
    }

    protected static final Logger logger = Logger.getLogger(LigerRefinementOperator.class.getName());

    public LigerRefinementOperator(ACache s, ACache t, double recall, long optTime, LinkSpecification spec) {
        super(s, t, recall, optTime, spec);
    }

    /**
     * Implements the basic idea of LIGER: Link Discovery with Guaranteed
     * Recall. The algorithm's main loop starts and run until the total
     * optimization time has exceeded maxOpt, the buffer or unrefined
     * specification is empty or the selectivity of the best subsumed LS is
     * equal to the desired selectivity. At the beginning of each iteration, the
     * first element of the buffer queue is popped out and the algorithm checks
     * if it receives a better run time score compared to the best
     * specification's runtime estimation. If the new specifications achieves a
     * better runtime estimation, the algorithm assigns it as the best
     * specification. If the new best specification has achieved the desired
     * selectivity, the algorithm terminates.
     * 
     * Then, LIGER calls the function refine(), which implements the refinement
     * operator. A new subsumed specification resulted from the refinement is
     * stored iff it does not already exists in total set and its selectivity is
     * higher or equal to the desired selectivity.
     * 
     * Finally, the algorithm performs a hierarchical ordering of the
     * specifications in the buffer queue with respect to runtime estimations.
     * 
     */
    @Override
    public void optimize() {
        long totalOptimizationTime = System.currentTimeMillis() + this.maxOpt;

        while (this.buffer.size() != 0 && System.currentTimeMillis() < totalOptimizationTime) {

            PartialRecallRefinementNode currentNode = buffer.poll();
            if (currentNode == null)
                break;

            LinkSpecification currentLSClone = currentNode.getLinkSpecification().clone();
            Plan currentPlan = currentNode.getPlan();

            LinkSpecification bestLS = best.getLinkSpecification();
            Plan bestPlan = best.getPlan();

            if (!currentLSClone.toString().equals(bestLS.toString())) {
                int com = RuntimeComparison.comparePlans(currentPlan, bestPlan);
                if (com < 0) {
                    best.setLinkSpecification(currentLSClone);
                    best.setPlan(currentPlan);
                    int cSel = checkSelectivity(best.getPlan().getSelectivity());
                    // LS with selectivity lower than the desired
                    // will NEVER be added to the buffer
                    if (cSel == 0) {
                        break;
                    }
                }
            }
            List<LinkSpecification> newLSs = refine(currentLSClone);

            newNodes = new HashMap<LinkSpecification, Plan>();
            addSpecifications(newLSs);

            this.total.add(currentLSClone.toString());

            if (newNodes.size() != 0) {
                LinkedList<PartialRecallRefinementNode> sorted = RuntimeComparison.sortLinkSpecifications(newNodes);
                buffer.addAll(0, sorted);
            }

        }
    }

    /**
     * Creates the set of to-be-refined nodes by adding specification that does
     * not already exists in total set and their selectivity is higher or equal
     * to the desired selectivity.
     * 
     * @param specs,
     *            the input set of unchecked specifications
     */
    public void addSpecifications(List<LinkSpecification> specs) {
        for (LinkSpecification sp : specs) {
            if (!this.total.contains(sp.toString())) {
                LigerPlanner planner = new LigerPlanner(this.source, this.target);
                Plan plan = planner.plan(sp);
                int cSel = checkSelectivity(plan.getSelectivity());
                if (cSel >= 0) {
                    newNodes.put(sp, plan);
                }
                this.total.add(sp.toString());

            }
        }

    }

    /**
     * Implements the refinement operator for guaranteed recall.
     * 
     * 
     * @param currentSpec,
     *            an input specification
     * @return a list of refined specifications
     */
    public List<LinkSpecification> refine(LinkSpecification currentSpec) {

        List<LinkSpecification> temp = new ArrayList<LinkSpecification>();
        if (currentSpec.isAtomic()) {
            LinkSpecification newLS = refineAtomicLinkSpecification(currentSpec);
            if (newLS != null)
                temp.add(newLS);

        } else {
            // refine left child
            List<LinkSpecification> l = merge(currentSpec, refine(currentSpec.getChildren().get(0)),
                    new ArrayList<>(Arrays.asList(currentSpec.getChildren().get(1))), true);
            temp.addAll((ArrayList<LinkSpecification>) l);

            // refine right child
            List<LinkSpecification> r = null;
            if (!currentSpec.getOperator().equals(LogicOperator.MINUS)) {
                r = merge(currentSpec, new ArrayList<>(Arrays.asList(currentSpec.getChildren().get(0))),
                        refine(currentSpec.getChildren().get(1)), false);
                temp.addAll((ArrayList<LinkSpecification>) r);

            }
            if (currentSpec.getOperator().equals(LogicOperator.OR)) {

                LinkSpecification currentSpecCloneL = currentSpec.clone();
                LinkSpecification leftChildClone = currentSpecCloneL.getChildren().get(0);
                if (currentSpecCloneL.getThreshold() > leftChildClone.getThreshold()) {
                    leftChildClone.setThreshold(currentSpecCloneL.getThreshold());
                }
                ///////////////////////////////////////////////////////////
                LinkSpecification currentSpecCloneR = currentSpec.clone();
                LinkSpecification rightChildClone = currentSpecCloneR.getChildren().get(1);
                if (currentSpecCloneR.getThreshold() > rightChildClone.getThreshold()) {
                    rightChildClone.setThreshold(currentSpecCloneR.getThreshold());
                }
                temp.add(leftChildClone);
                temp.add(rightChildClone);
            }
        }

        return temp;

    }

    /**
     * Refines an atomic link specification by calling the next(threshold)
     * function.
     * 
     * @param currentSpec,an
     *            input specification
     * @return null if the currentSpec can not be refine anymore, or a refined
     *         atomic specification
     */
    public LinkSpecification refineAtomicLinkSpecification(LinkSpecification currentSpec) {
        if (currentSpec.isAtomic() == false) {
            return null;
        }
        LinkSpecification clone = new LinkSpecification();
        double newThreshold = next(currentSpec.getThreshold());

        if (newThreshold == -1.0d) {// don't add it as a plan
            clone = null;
        } else {
            clone = currentSpec.clone();
            clone.setThreshold(newThreshold);
        }
        return clone;
    }

    /**
     * Creates a set of new specification by cloning the input specification,
     * keeping one child specification as is, and substitutes the other child
     * specification with the set of specification retrieved by refining the
     * other child.
     * 
     * @param parent,
     *            an input specification
     * @param leftChildren,
     *            a set of left (refined) children specifications
     * @param rightChildren,
     *            a set of right (refined) children specifications
     * @param isLeft,
     *            flag that indicates with child, left or right, has been
     *            refined previously
     * @return a set of specifications subsumed by the parent specification
     */
    public List<LinkSpecification> merge(LinkSpecification parent, List<LinkSpecification> leftChildren,
            List<LinkSpecification> rightChildren, boolean isLeft) {

        List<LinkSpecification> specList = new ArrayList<LinkSpecification>();

        // clone parent
        // clone both children
        // set clone's children to null
        // set clones' children as the clones of the original children
        // set child.parent = clone for both children

        if (isLeft) {// left child changed
            for (LinkSpecification leftChild : leftChildren) {
                for (LinkSpecification rightChild : rightChildren) {

                    LinkSpecification parentClone = parent.clone();
                    LinkSpecification leftChildClone = leftChild.clone();
                    LinkSpecification rightChildClone = rightChild.clone();
                    // add clones of children
                    parentClone.setChildren(new ArrayList<LinkSpecification>());
                    parentClone.addChild(leftChildClone);
                    leftChildClone.setParent(parentClone);
                    parentClone.addChild(rightChildClone);
                    rightChildClone.setParent(parentClone);

                    specList.add(parentClone);
                }
            }

        } else {// left child changed
            for (LinkSpecification rightChild : rightChildren) {
                for (LinkSpecification leftChild : leftChildren) {
                    LinkSpecification parentClone = parent.clone();
                    LinkSpecification leftChildClone = leftChild.clone();
                    LinkSpecification rightChildClone = rightChild.clone();
                    // add clones of children
                    parentClone.setChildren(new ArrayList<LinkSpecification>());
                    parentClone.addChild(leftChildClone);
                    leftChildClone.setParent(parentClone);
                    parentClone.addChild(rightChildClone);
                    rightChildClone.setParent(parentClone);

                    specList.add(parentClone);

                }
            }
        }
        return specList;

    }

}
