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
package org.aksw.limes.core.measures.measure.phoneticmeasure;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PhoneticMeasureTest {

    @Test
    public void testCaverphone1() throws Exception {
        Caverphone1Measure caver1 = new Caverphone1Measure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(caver1.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }

    @Test
    public void testCaverphone2() throws Exception {
        Caverphone2Measure caver2 = new Caverphone2Measure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(caver2.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }


    @Test
    public void testMetaphoneMeasure() throws Exception {
        MetaphoneMeasure metaphone = new MetaphoneMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(metaphone.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }

    @Test
    public void testDoubleMetaphoneMeasure() throws Exception {
        DoubleMetaphoneMeasure doublemetaphone = new DoubleMetaphoneMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(doublemetaphone.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }


    @Test
    public void testRefinedSoundexMeasure() throws Exception {
        RefinedSoundexMeasure rsoundex = new RefinedSoundexMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(rsoundex.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }

    @Test
    public void testNysiisMeasure() throws Exception {
        NysiisMeasure nysiis = new NysiisMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(nysiis.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }


    @Test
    public void testMatchRatingApproachEncoderMeasure() throws Exception {
        MatchRatingApproachEncoderMeasure match = new MatchRatingApproachEncoderMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(match.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }

    @Test
    public void testKoelnPhoneticMeasure() throws Exception {
        KoelnPhoneticMeasure koeln = new KoelnPhoneticMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(koeln.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }

    @Test
    public void testSoundexMeasure() throws Exception {
        SoundexMeasure soundex = new SoundexMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(soundex.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }

    @Test
    public void testDaitchMokotoffSoundexMeasure() throws Exception {
        DaitchMokotoffSoundexMeasure daitch = new DaitchMokotoffSoundexMeasure();

        Set<String> a = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Mohammed");
        Set<String> b = new HashSet<>();
        a.add("Abdullah");
        a.add("Ahmed");
        a.add("Ali");
        for(String str1: a) {
            for(String str2: b) {
                assertEquals(daitch.getSimilarity(str1, str2), 0.5d, 0.d);
            }
        }
    }
}
