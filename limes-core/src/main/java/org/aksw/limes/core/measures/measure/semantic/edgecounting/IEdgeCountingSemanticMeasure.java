package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.IMeasure;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;

public interface IEdgeCountingSemanticMeasure extends IMeasure {
    /**
     * Returns the IIndexWord representation of a String
     * 
     * @param str1
     * @return IIndexWord representation of str1
     */
    public IIndexWord getIIndexWord(String str1);

    /**
     * Calculates the semantic similarity of two synsets.
     * 
     * @param synsets1
     * @param synsets2
     * @return semantic similarity of two synsets
     */
    public double getSimilarity(ISynset synset1, List<List<ISynset>> synset1Tree, ISynset synset2, List<List<ISynset>> synset2Tree);

    /**
     * Calculates the semantic similarity of two words.
     * 
     * @param w1
     * @param w2
     * @return semantic similarity of two words
     */
    public double getSimilarity(IIndexWord w1, IIndexWord w2);

    /**
     * Get all possible trees from the root of the hierarchy to the synset
     * 
     * @param synset
     * @return the list of hypernym trees for the synset
     */
    public List<List<ISynset>> getHypernymTrees(ISynset synset);

}
