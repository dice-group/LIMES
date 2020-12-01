package org.aksw.limes.core.execution.planning.planner;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RunWith(Suite.class)
@SuiteClasses({PlannerSuite.class})
public class PlannerRunner {

    private static final Logger logger = LoggerFactory.getLogger(PlannerRunner.class);
    public void main() {
        Result result = JUnitCore.runClasses(PlannerSuite.class);
        logger.info("{}",result.wasSuccessful());
        //TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
        //TestResult result = new TestResult();
        //suite.run(result);
    }
}