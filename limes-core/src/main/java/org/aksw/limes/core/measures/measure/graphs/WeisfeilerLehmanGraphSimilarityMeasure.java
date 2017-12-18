package org.aksw.limes.core.measures.measure.graphs;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.aksw.jena_sparql_api.example.Example;
import org.aksw.limes.core.io.cache.Instance;
import org.apache.spark.mllib.linalg.SparseVector;
import weka.gui.graphvisualizer.GraphNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeisfeilerLehmanGraphSimilarityMeasure extends AGraphSimilarityMeasure {


  @Override
  public double getSimilarity(Object object1, Object object2) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double getSimilarity(Instance instance1, Instance instance2, String property1,
      String property2) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double getRuntimeApproximation(double mappingSize) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}

