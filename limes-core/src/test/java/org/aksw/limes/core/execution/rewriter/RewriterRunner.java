package org.aksw.limes.core.execution.rewriter;


import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AlgebraicRewriterTest.class})
public class RewriterRunner {

    public void main() {
        Result result = JUnitCore.runClasses(RewriterSuite.class);
        System.out.println(result.wasSuccessful());
        //TestSuite suite = new TestSuite(ExecutionEngineFactoryTest.class);
        //TestResult result = new TestResult();
        //suite.run(result);
    }

}
