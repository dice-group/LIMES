package org.aksw.limes.core.execution.engine.filter;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({LinearFilterSuite.class})
public class LinearFilterRunner {

    public void main() {
        Result result = JUnitCore.runClasses(LinearFilterSuite.class);
        System.out.println(result.wasSuccessful());
        //TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
        //TestResult result = new TestResult();
        //suite.run(result);
    }

}