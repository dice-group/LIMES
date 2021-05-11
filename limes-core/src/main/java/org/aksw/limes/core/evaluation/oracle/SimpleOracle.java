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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a naive oracle
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class SimpleOracle implements IOracle {
    static Logger logger = LoggerFactory.getLogger(SimpleOracle.class);
    /** The mapping contains the oracle instances */
    AMapping mapping;

    public SimpleOracle() {

    }

    public SimpleOracle(AMapping m) {
        loadData(m);
    }

    /** check if a pair source-target URIs exist in the oracle
     * @param uri1 the source URI
     * @param uri2 the target URI
     * @return boolean -  rue in case of existence otherwise false */

    public boolean ask(String uri1, String uri2) {
        //         System.out.println(uri1 + "<->" + uri2);
        if (mapping == null) return false;
        return (mapping.contains(uri1, uri2) || mapping.contains(uri2, uri1));
    }

    /** Loads the oracle with the given mapping
     * @param m the source mapping to assign to oracle*/

    public void loadData(AMapping m) {
        mapping = m;
        //     System.out.println(m);
    }

    /** It returns the size of mapping
     * @return  int - the size of the mapping*/
    public int size() {
        return mapping.size();
    }

    public AMapping getMapping() {
        return mapping;
    }

    /** It returns the type of mapping
     * @return  String - teh type of the mapping*/

    public String getType() {
        return "simple";
    }

}
