package org.aksw.limes.core.execution.planning.planner;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlannerFactoryTest {

    @Test
    public void testEqualDefault() {
	IPlanner planner = ExecutionPlannerFactory.getPlanner("cannonical", null, null);
	assertTrue(planner instanceof CanonicalPlanner);
    }
    @Test
    public void testEqualHelios() {
	IPlanner planner = ExecutionPlannerFactory.getPlanner("helios", null, null);
	assertTrue(planner instanceof HeliosPlanner);
    }
    
    @Test
    public void testNotEqualHelios() {
	IPlanner planner = ExecutionPlannerFactory.getPlanner("cannonical", null, null);
	assertFalse(planner instanceof HeliosPlanner);
    }

}
