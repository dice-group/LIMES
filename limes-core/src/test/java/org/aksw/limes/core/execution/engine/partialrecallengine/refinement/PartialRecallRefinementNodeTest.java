package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import static org.junit.Assert.*;

import org.aksw.limes.core.execution.engine.partialrecallengine.refinement.PartialRecallRefinementNode;
import org.junit.Test;

public class PartialRecallRefinementNodeTest {

    @Test
    public void empty() {
        PartialRecallRefinementNode node1 = new PartialRecallRefinementNode(null, null);
        assertTrue(node1.getLinkSpecification().isEmpty());
        assertTrue(node1.getPlan().isEmpty());
        
    }

}
