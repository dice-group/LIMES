package org.aksw.limes.core.measures.measure.semantic.edgecounting;


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

    public double getSimilarityBetweenSynsets(ISynset synset1, ISynset synset2);

    


}
