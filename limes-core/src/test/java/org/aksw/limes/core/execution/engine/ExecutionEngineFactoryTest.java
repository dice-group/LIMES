/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.engine.partialrecallengine.PartialRecallExecutionEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


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
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, null, null, null, null, 0, 1.0);
        assertTrue(engine instanceof SimpleExecutionEngine);
    }

    @Test
    public void testEqualLiger() {
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.PARTIAL_RECALL, null, null, null, null, 0, 1.0);
        assertTrue(engine instanceof PartialRecallExecutionEngine);
    }
}
