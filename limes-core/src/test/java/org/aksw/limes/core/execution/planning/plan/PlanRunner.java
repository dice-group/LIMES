package org.aksw.limes.core.execution.planning.plan;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({PlanSuite.class})
public class PlanRunner {

    public void main() {
        Result result = JUnitCore.runClasses(PlanSuite.class);
        System.out.println(result.wasSuccessful());
        //TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
        //TestResult result = new TestResult();
        //suite.run(result);
    }
}