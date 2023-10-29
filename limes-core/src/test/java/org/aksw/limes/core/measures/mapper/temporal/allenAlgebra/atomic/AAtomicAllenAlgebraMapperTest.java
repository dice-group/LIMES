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
package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class AAtomicAllenAlgebraMapperTest {

    @Test
    public void test() {


        String[] values = new String[]{
                "2015-05-20T08:21:04.123Z",
                "2015-05-20T08:21:04.123+02:00",

                "2015-05-20T08:21:04.598",

                "2015-05-20T08:21:04Z",
                "2015-05-20T08:21:04+02:00",

                "2015-05-20T08:21:04",

                "2015-05-20T08:21Z",
                "2015-05-20T08:21+02:00",

                "2015-05-20T08:21",
                "2015-05-20T08:21:00",

                //"2015-05-20",

                //"2015-05",

                //"2015",

        };

        ArrayList<Long> numbers = new ArrayList<Long>();

        for(String value: values){
            long epoch = AAtomicAllenAlgebraMapper.getEpoch(value);
            System.out.println(epoch);
            numbers.add(epoch);
            System.out.println("-----------------------");
        }
        //7200000
        assertTrue(numbers.get(0)-numbers.get(1) == 7200000);

        assertTrue(numbers.get(3)-numbers.get(4) == 7200000);

        assertTrue(numbers.get(6)-numbers.get(7) == 7200000);

        assertTrue(numbers.get(8).equals(numbers.get(9)));

    }

}
