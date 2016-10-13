package org.aksw.limes.core.execution.engine;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExecutionEngineFactoryTest {


    public ExecutionEngineFactoryTest() {
    }


    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testEqualDefault() {
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, null, null, null, null);
        assertTrue(engine instanceof SimpleExecutionEngine);
    }


}
