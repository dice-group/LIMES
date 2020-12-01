package org.aksw.limes.core.execution.planning.plan;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Suite.class)
@SuiteClasses({PlanSuite.class})
public class PlanRunner {

    private static final Logger logger = LoggerFactory.getLogger(PlanRunner.class);

    public void main() {
        Result result = JUnitCore.runClasses(PlanSuite.class);
        logger.info("{}",result.wasSuccessful());
        //TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
        //TestResult result = new TestResult();
        //suite.run(result);
    }
}