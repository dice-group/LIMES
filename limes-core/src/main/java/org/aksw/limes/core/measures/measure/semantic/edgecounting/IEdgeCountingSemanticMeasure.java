package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.ArrayList;

import org.aksw.limes.core.measures.measure.semantic.ISemanticMeasure;

import edu.mit.jwi.item.ISynset;

public interface IEdgeCountingSemanticMeasure extends ISemanticMeasure {

    /**
     * Calculates the semantic similarity of two synsets using their synset
     * paths.
     * 
     * @param synsets1
     * @param synsets2
     * @return semantic similarity of two synsets
     */

    public double getSimilarityComplex(ISynset synset1, ISynset synset2);

    /**
     * Calculates the semantic similarity of two synsets WITHOUT using their
     * synset paths.
     * 
     * @param synsets1
     * @param synsets2
     * @return semantic similarity of two synsets
     */
    public double getSimilaritySimple(ISynset synset1, ISynset synset2);

    public boolean filter(ArrayList<Integer> parameters);

}
