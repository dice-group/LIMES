package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements the Refinement Node for the refinement tree used in the partial
 * recall execution engine. The Refinement Node consists of a link specification
 * and its corresponding plan. There is correspondence between a link
 * specification and its plan.
 * 
 * @author Kleanthi Georgala
 *
 */
public class PartialRecallRefinementNode {

    private LinkSpecification spec;
    private Plan plan;

    public PartialRecallRefinementNode(LinkSpecification sp, Plan p) {
        this.spec = sp;
        this.plan = p;
    }

    public LinkSpecification getLinkSpecification() {
        return this.spec;
    }

    public void setLinkSpecification(LinkSpecification sp) {
        this.spec = sp;
    }

    public Plan getPlan() {
        return this.plan;
    }

    public void setPlan(Plan p) {
        this.plan = p;
    }
}
