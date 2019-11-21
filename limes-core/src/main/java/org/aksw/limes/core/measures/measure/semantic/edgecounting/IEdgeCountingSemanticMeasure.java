package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import org.aksw.limes.core.measures.measure.IMeasure;

import edu.mit.jwi.item.ISynset;

/**
 * Implements the edge-counting semantic string similarity interface.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IEdgeCountingSemanticMeasure extends IMeasure {

    /**
     * Calculates the semantic similarity between two concepts.
     * 
     * @param synsets1,
     *            the first input concept
     * @param synsets2,
     *            the second input concept
     * @return the semantic similarity of two concepts
     */

    public double getSimilarityBetweenConcepts(ISynset synset1, ISynset synset2);

}
