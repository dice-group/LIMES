package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;

public abstract class AStringMeasure extends AMeasure implements IStringMeasure {

  public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
    double sim = 0;
    double max = 0;
    for (String p1 : instance1.getProperty(property1)) {
      for (String p2 : instance2.getProperty(property2)) {
        sim = getSimilarity(p1, p2);
        if (max < sim) {
          max = sim;
        }
      }
    }
    return max;
  }

  public String getType() {
    return "string";
  }

}
