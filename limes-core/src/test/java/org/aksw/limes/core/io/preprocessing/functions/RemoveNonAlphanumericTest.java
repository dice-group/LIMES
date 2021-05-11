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
package org.aksw.limes.core.io.preprocessing.functions;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveNonAlphanumericTest {

    public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

    // =============== EXPECTED VALUES ==================================
    public static final String REMOVE_NON_ALPHANUMERIC_EXPECTED = "alphanumeric";

    // =============== PROPERTIES =======================================
    public static final String PROP_NON_ALPHANUMERIC = "nonalphanumeric";

    // =============== VALUES ===========================================
    public static final String PROP_NON_ALPHANUMERIC_VALUE = "a!lp%h|a^n&u*m<e)r=ic";

    public Instance testInstance;

    @Before
    public void prepareData() {
        testInstance = new Instance(TEST_INSTANCE);

        testInstance.addProperty(PROP_NON_ALPHANUMERIC, PROP_NON_ALPHANUMERIC_VALUE);
    }

    @Test
    public void testRemoveNonAlphanumeric() throws IllegalNumberOfParametersException {
        new RemoveNonAlphanumeric().applyFunction(testInstance, PROP_NON_ALPHANUMERIC);
        assertEquals(REMOVE_NON_ALPHANUMERIC_EXPECTED, testInstance.getProperty(PROP_NON_ALPHANUMERIC).first());
    }
}
