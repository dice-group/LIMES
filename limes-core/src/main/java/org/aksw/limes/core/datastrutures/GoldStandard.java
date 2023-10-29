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
package org.aksw.limes.core.datastrutures;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

import java.util.List;

/**
 * This class contains the gold standard mapping and the source and target URIs
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class GoldStandard {
    /**
     * the mapping of the gold standard
     */
    public AMapping referenceMappings;
    /** a list of the source dataset URIs*/
    public List<String> sourceUris;
    /** a list of the target dataset URIs*/
    public List<String> targetUris;

    public GoldStandard(AMapping reference, List<String> sourceUris, List<String> targetUris) {
        super();
        this.referenceMappings = reference;
        this.sourceUris = sourceUris;
        this.targetUris = targetUris;
    }

    public GoldStandard(AMapping reference, ACache sourceUris, ACache targetUris) {
        super();
        this.referenceMappings = reference;
        this.sourceUris = sourceUris.getAllUris();
        this.targetUris = targetUris.getAllUris();
    }

    public GoldStandard(AMapping m) {
        this.referenceMappings = m;
    }

}
