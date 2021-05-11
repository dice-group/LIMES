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
package org.aksw.limes.core.measures.mapper.pointsets;


import org.junit.Test;

public class OrchidMapperTest {

    @Test
    public void test() {
        System.out.println(OrchidMapper.getPoints("MULTILINESTRING((129.1656696 43.1537336, 129.1653388 43.1494863), (29.1656696 43.1537336, 29.1653388 43.1494863))"));
        System.out.println(OrchidMapper.getPoints("POINT(-79.116667 -3.2)"));
        System.out.println(OrchidMapper.getPoints("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        System.out.println(OrchidMapper.getPoints("<http://www.opengis.net/def/crs/EPSG/0/4326> POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
    }

}
