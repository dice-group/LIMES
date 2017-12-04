package org.aksw.limes.core.measures.mapper.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.string.SimpleEditDistanceMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * A very minimal (and rather inefficient) mapper. It simply compares all pairs of values with each other.
 * The advantage is that it works with any string measure, so it's a good way to quickly test a new string
 * measure without writing a new, separate, and optimized mapper class for it
 */
public class SimpleGeneralMapper extends AMapper {

  static Logger logger = LoggerFactory.getLogger(SimpleGeneralMapper.class);
  private AMeasure measure;

  /**
   * @param measure The measure to be used for string similarity evaluation.
   */
  public SimpleGeneralMapper(AMeasure measure) {
    this.measure = measure;
  }

  /**
   * Computes a mapping between a source and a target.
   *
   * @param source
   *            Source cache
   * @param target
   *            Target cache
   * @param sourceVar
   *            Variable for the source dataset
   * @param targetVar
   *            Variable for the target dataset
   * @param expression
   *            Expression to process.
   * @param threshold
   *            Similarity threshold
   * @return A mapping which contains links between the source instances and
   *         the target instances
   */
  @Override
  public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
      double threshold) {
    if (threshold <= 0) {
      throw new InvalidThresholdException(threshold);
    }
    List<String> properties = PropertyFetcher.getProperties(expression, threshold);
    // if no properties then terminate
    if (properties.get(0) == null || properties.get(1) == null) {
      logger.error(MarkerFactory.getMarker("FATAL"), "Property values could not be read. Exiting");
      throw new RuntimeException();
    }
    Map<String, Set<String>> sourceIndex = getValueToUriMap(source, properties.get(0));
    Map<String, Set<String>> targetIndex = getValueToUriMap(target, properties.get(1));
    AMapping m = MappingFactory.createDefaultMapping();
    for (String x : sourceIndex.keySet()) {
      for (String y : targetIndex.keySet()) {
        double sim = measure.getSimilarity(x,y);
        if (sim >= threshold) {
          for (String sourceUri : sourceIndex.get(x)) {
            for (String targetUri : targetIndex.get(y)) {
              m.add(sourceUri, targetUri, sim);
            }
          }
        }
      }
    }
    return m;
  }

  public Map<String, Set<String>> index(ACache c, String property) {
    Map<String, Set<String>> index = new HashMap<String, Set<String>>();
    for (String uri : c.getAllUris()) {
      TreeSet<String> values = c.getInstance(uri).getProperty(property);
      for (String v : values) {
        if (!index.containsKey(v)) {
          index.put(v, new HashSet<>());
        }
        index.get(v).add(uri);
      }
    }
    return index;
  }

  @Override
  public String getName() {
    return "simpleGeneral";
  }

  @Override
  public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
    return 1000d;
  }

  @Override
  public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
    return 1000d;
  }
}
