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

public class CleanIriTest {

    public static final String TEST_INSTANCE = "http://dbpedia.org/resource/Ibuprofen";

    // =============== EXPECTED VALUES ==================================
    public static final String CLEAN_IRI_EXPECTED = "label";

    // =============== PROPERTIES =======================================
    public static final String PROP_IRI = "iri";
    public static final String PROP_IRI_NO_HASHTAG = "irino#";

    // =============== VALUES ===========================================
    public static final String PROP_IRI_VALUE = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String PROP_IRI_NO_HASHTAG_VALUE = "http://www.w3.org/2000/01/rdf-schema/label";

    public Instance testInstance;

    @Before
    public void prepareData() {
        testInstance = new Instance(TEST_INSTANCE);

        testInstance.addProperty(PROP_IRI, PROP_IRI_VALUE);
        testInstance.addProperty(PROP_IRI_NO_HASHTAG, PROP_IRI_NO_HASHTAG_VALUE);
    }

    @Test
    public void testCleanIri() throws IllegalNumberOfParametersException {
        new CleanIri().applyFunction(testInstance, PROP_IRI);
        assertEquals(CLEAN_IRI_EXPECTED, testInstance.getProperty(PROP_IRI).first());
    }

    @Test
    public void testCleanIriNoHashtag() throws IllegalNumberOfParametersException {
        new CleanIri().applyFunction(testInstance, PROP_IRI_NO_HASHTAG);
        assertEquals(CLEAN_IRI_EXPECTED, testInstance.getProperty(PROP_IRI_NO_HASHTAG).first());
    }
}
