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

public class RegexReplaceTest {

    public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

    // =============== EXPECTED VALUES ==================================
    public static final String REGEX_REPLACE_EXPECTED = "Ibuprofen is a nonsteroidal anti-inflammatory drug derivative of propionic acid used for relieving pain, helping with fever and reducing inflammation.";

    // =============== PROPERTIES =======================================
    public static final String PROP_ABSTRACT = "dbo:abstract";

    // =============== VALUES ===========================================
    public static final String PROP_ABSTRACT_VALUE = "Ibuprofen (/ˈaɪbjuːproʊfɛn/ or /aɪbjuːˈproʊfən/ EYE-bew-PROH-fən; from isobutylphenylpropanoic acid) is a nonsteroidal anti-inflammatory drug (NSAID) derivative of propionic acid used for relieving pain, helping with fever and reducing inflammation.@en";

    // Used for RegexReplaceTest Removes everything inside braces and language
    // tag
    public static final String REGEX = "\\((.*?)\\) |@\\w*";
    public Instance testInstance;

    @Before
    public void prepareData() {
        testInstance = new Instance(TEST_INSTANCE);

        testInstance.addProperty(PROP_ABSTRACT, PROP_ABSTRACT_VALUE);
    }

    @Test
    public void testRegexReplace() throws IllegalNumberOfParametersException {
        new RegexReplace().applyFunction(testInstance, PROP_ABSTRACT, REGEX, "");
        assertEquals(REGEX_REPLACE_EXPECTED, testInstance.getProperty(PROP_ABSTRACT).first());
    }
}
