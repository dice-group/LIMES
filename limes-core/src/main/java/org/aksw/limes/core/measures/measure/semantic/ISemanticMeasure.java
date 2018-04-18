package org.aksw.limes.core.measures.measure.semantic;


import org.aksw.limes.core.measures.measure.IMeasure;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;

public interface ISemanticMeasure extends IMeasure {
    /**
     * Calculates the semantic similarity of two synsets.
     * 
     * @param synsets1
     * @param synsets2
     * @return semantic similarity of two synsets
     */
    public double getSimilarity(ISynset synset1, ISynset synset2);

    /**
     * Calculates the semantic similarity of two words.
     * 
     * @param w1
     * @param w2
     * @return semantic similarity of two words
     */
    public double getSimilarity(IIndexWord w1, IIndexWord w2);
}
