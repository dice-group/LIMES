package org.aksw.limes.core.measures.measure.graphs;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;

public abstract class AGraphSimilarityMeasure extends AMeasure implements IGraphSimilarityMeasure {


  public String getType() {
    return "graph";
  }

}
