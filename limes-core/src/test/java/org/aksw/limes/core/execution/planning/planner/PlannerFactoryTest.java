package org.aksw.limes.core.execution.planning.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.junit.Test;

public class PlannerFactoryTest {

    @Test
    public void testEqualDefault() {
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, null, null);
        assertTrue(planner instanceof CanonicalPlanner);
    }

    @Test
    public void testEqualHelios() {
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.HELIOS, null, null);
        assertTrue(planner instanceof HeliosPlanner);
    }

    @Test
    public void testNotEqualHelios() {
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, null, null);
        assertFalse(planner instanceof HeliosPlanner);
    }

}
