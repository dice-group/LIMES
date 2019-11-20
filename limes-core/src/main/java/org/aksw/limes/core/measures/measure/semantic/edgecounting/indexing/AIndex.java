package org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing;

import java.util.ArrayList;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

public abstract class AIndex {
 

    public abstract void preIndex();
    
    public abstract void init(boolean f);

    public abstract void close();

    public abstract int getMinDepth(ISynset synset) ;

    public abstract int getMaxDepth(ISynset synset);

    public abstract ArrayList<ArrayList<ISynsetID>> getHypernymPaths(ISynset synset);
}
