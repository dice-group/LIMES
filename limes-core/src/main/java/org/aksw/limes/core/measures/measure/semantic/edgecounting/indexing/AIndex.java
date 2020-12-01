package org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing;

import java.util.ArrayList;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

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
