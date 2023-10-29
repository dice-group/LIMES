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

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class RemoveLanguageTagTest {

    public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

    // =============== EXPECTED VALUES ==================================
    public static final String REPLACE_EXPECTED = "Ibuprofen";
    public static final String REPLACE_EXPECTED2 = "Ibuprofen@en";
    public static final String REMOVELANGUAGETAG_EXPECTED = "testext";

    // =============== PROPERTIES =======================================
    public static final String PROP_LABEL = "rdfs:label";
    public static final String PROP_TEST2 = "test2";

    // =============== VALUES ===========================================
    public static final String PROP_LABEL_VALUE1 = "Ibuprofen@de";
    public static final String PROP_LABEL_VALUE2 = "Ibuprofen@en";

    public Instance testInstance;

    @Before
    public void prepareData() {
        testInstance = new Instance(TEST_INSTANCE);

        TreeSet<String> labels = new TreeSet<>();
        labels.add(PROP_LABEL_VALUE1);
        labels.add(PROP_LABEL_VALUE2);
        testInstance.addProperty(PROP_LABEL, labels);

        testInstance.addProperty(PROP_TEST2, REMOVELANGUAGETAG_EXPECTED);
    }

    @Test
    public void testRemoveLanguageTag() throws IllegalNumberOfParametersException {
        new RemoveLanguageTag().applyFunction(testInstance, PROP_LABEL);
        assertEquals(1, testInstance.getProperty(PROP_LABEL).size());
        assertEquals(REPLACE_EXPECTED, testInstance.getProperty(PROP_LABEL).first());
    }

    @Test
    public void testRemoveLanguageTagNoTagPresent() throws IllegalNumberOfParametersException {
        new RemoveLanguageTag().applyFunction(testInstance, PROP_TEST2);
        assertEquals(1, testInstance.getProperty(PROP_TEST2).size());
        assertEquals(REMOVELANGUAGETAG_EXPECTED, testInstance.getProperty(PROP_TEST2).first());
    }

}
