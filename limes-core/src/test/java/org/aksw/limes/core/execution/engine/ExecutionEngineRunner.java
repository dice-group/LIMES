package org.aksw.limes.core.execution.engine;



import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestSuite;
@RunWith(Suite.class)
@SuiteClasses({DefaultExecutionEngineTest.class})
public class ExecutionEngineRunner {

    public void main() {
	Result result = JUnitCore.runClasses(DefaultExecutionEngineTest.class);
	System.out.println(result.wasSuccessful());
	//TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
	//TestResult result = new TestResult();
	//suite.run(result);
    }

}
