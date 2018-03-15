package org.aksw.limes.core.measures.mapper.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class EdgeCountingSemanticMapper extends AMapper {
    static Logger logger = LoggerFactory.getLogger(EdgeCountingSemanticMapper.class);

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
        // if expression not atomic, terminate
        Parser p = new Parser(expression, threshold);
        if (!p.isAtomic()) {
            logger.error(MarkerFactory.getMarker("FATAL"), "Mappers can only deal with atomic expression");
            logger.error(MarkerFactory.getMarker("FATAL"),
                    "Expression " + expression + " was given to a mapper to process");
        }

        AMapping m = MappingFactory.createDefaultMapping();
        MeasureType type = MeasureFactory.getMeasureType(p.getOperator());
        AEdgeCountingSemanticMeasure measure = (AEdgeCountingSemanticMeasure) MeasureFactory.createMeasure(type);

        for (Instance sourceInstance : source.getAllInstances()) {
            for (Instance targetInstance : target.getAllInstances()) {
                double sim = measure.getSimilarity(sourceInstance, targetInstance, properties.get(0),
                        properties.get(1));
                if (sim >= threshold) {
                    m.add(sourceInstance.getUri(), targetInstance.getUri(), sim);
                }
            }
        }
        return m;
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public String getName() {
        return "Semantic Mapper";
    }

}
