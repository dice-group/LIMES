package org.aksw.limes.core.execution.engine.filter;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RunWith(Suite.class)
@SuiteClasses({LinearFilterSuite.class})
public class LinearFilterRunner {
    private static final Logger logger = LoggerFactory.getLogger(LinearFilterRunner.class);

    public void main() {
        Result result = JUnitCore.runClasses(LinearFilterSuite.class);
        logger.info("{}",result.wasSuccessful());
        //TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
        //TestResult result = new TestResult();
        //suite.run(result);
    }

}