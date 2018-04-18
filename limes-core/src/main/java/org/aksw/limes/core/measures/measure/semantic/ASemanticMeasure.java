package org.aksw.limes.core.measures.measure.semantic;


import org.aksw.limes.core.measures.measure.AMeasure;

import edu.mit.jwi.item.IIndexWord;

public abstract class ASemanticMeasure extends AMeasure implements ISemanticMeasure{
    /**
     * Returns the IIndexWord representation of a String
     * 
     * @param str1
     * @return IIndexWord representation of str1
     */
    public abstract IIndexWord getIIndexWord(String str1);

    
}
