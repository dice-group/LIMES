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
package org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

import java.util.ArrayList;

/**
 * Implements the Index interface.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class AIndex {

    /**
     * Stores all necessary information into memory
     *
     */
    public abstract void preIndex();

    /**
     * Initiliazes the Index instance
     *
     */
    public abstract void init(boolean f);

    /**
     * Closes the Index instance
     *
     */
    public abstract void close();

    /**
     * Retrieves all hypernym paths of a synset from memory
     *
     * @param synset,
     *            the input synset
     *
     * @return a list of all hypernym paths
     *
     */
    public abstract ArrayList<ArrayList<ISynsetID>> getHypernymPaths(ISynset synset);
}
