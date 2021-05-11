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
package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements the default rewriter class. The input link specification is
 * returned without any modification.
 *
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class DefaultRewriter extends Rewriter {
    /**
     * Constructor for the default rewriter.
     *
     */
    public DefaultRewriter() {
    }

    /**
     * Rewrites a Link Specification. No modification to the input link
     * specification.
     *
     * @param spec,
     *            Input link specification
     * @return the input link specification.
     */
    @Override
    public LinkSpecification rewrite(LinkSpecification spec) {
        if (spec.isEmpty())
            throw new IllegalArgumentException();
        return spec;
    }

}
