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
package org.aksw.limes.core.evaluation.oracle;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Basic idea is that the interface can load reference data and act as a user in simulations
 * This data will be mostly given as a mapping
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */

public interface IOracle {
    /**
     * Returns true if the mapping contains the two URIs, else false
     *
     * @param uri1
     *         First instance in instance pair
     * @param uri2
     *         Second instance in instance pair
     * @return boolean - true if exist otherwise false
     */
    public boolean ask(String uri1, String uri2);

    public void loadData(AMapping m);

    public int size();

    public AMapping getMapping();

    public String getType();
}
