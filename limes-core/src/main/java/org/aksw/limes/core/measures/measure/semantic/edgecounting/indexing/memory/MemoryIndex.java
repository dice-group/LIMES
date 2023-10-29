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
package org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.memory;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.finders.HypernymPathsFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Implements the memory index class that computes, stores and loads the
 * hypernym paths of every synset in wordnet.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class MemoryIndex extends AIndex {
    private static final Logger logger = LoggerFactory.getLogger(MemoryIndex.class);

    HashMap<String, HashMap<Integer, Integer>> minDepths = new HashMap<String, HashMap<Integer, Integer>>();
    HashMap<String, HashMap<Integer, Integer>> maxDepths = new HashMap<String, HashMap<Integer, Integer>>();

    HashMap<String, HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>> paths = new HashMap<String, HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>>();

    protected SemanticDictionary dictionary = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(boolean f) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preIndex() {
        this.preIndexPaths();

    }

    /**
     * Stores in memory all possible hypernym paths of a wordnet's synset from
     * the root(s).
     *
     *
     */
    public void preIndexPaths() {
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();

        logger.info("Indexing begins.");
        for (POS pos : POS.values()) {
            paths.put(pos.toString(), new HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>());
            Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
            while (iterator.hasNext()) {
                ISynset synset = iterator.next();
                ArrayList<ArrayList<ISynsetID>> trees = HypernymPathsFinder.getHypernymPaths(dictionary, synset);

                HashMap<Integer, ArrayList<ArrayList<ISynsetID>>> temp = paths.get(pos.toString());
                temp.put(synset.getOffset(), trees);
                paths.put(pos.toString(), temp);
            }
        }
        logger.info("Indexing done.");
        dictionary.removeDictionary();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ArrayList<ISynsetID>> getHypernymPaths(ISynset synset) {
        return paths.get(synset.getPOS().toString()).get(synset.getOffset());
    }

}
