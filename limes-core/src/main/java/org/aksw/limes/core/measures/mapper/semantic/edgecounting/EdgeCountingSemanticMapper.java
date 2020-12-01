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
import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.factory.SemanticFactory;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.factory.SemanticType;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.memory.MemoryIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class EdgeCountingSemanticMapper extends AMapper {
    static Logger logger = LoggerFactory.getLogger(EdgeCountingSemanticMapper.class);

    AEdgeCountingSemanticMeasure measure = null;
    AIndex Indexer = null;

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
        // if expression not atomic, terminate
        Parser p = new Parser(expression, threshold);
        if (!p.isAtomic()) {
            logger.error(MarkerFactory.getMarker("FATAL"), "Mappers can only deal with atomic expression");
            logger.error(MarkerFactory.getMarker("FATAL"),
                    "Expression " + expression + " was given to a mapper to process");
        }

        AMapping m = MappingFactory.createDefaultMapping();
        // create index before anything
        Indexer = new MemoryIndex();
        Indexer.preIndex();
        // create semantic similarity, pass indexer as parameter
        SemanticType type = SemanticFactory.getMeasureType(expression);
        measure = SemanticFactory.createMeasure(type, Indexer);

        for (Instance sourceInstance : source.getAllInstances()) {
            // System.out.println("Source URI "+sourceInstance.getUri());
            for (Instance targetInstance : target.getAllInstances()) {
                // System.out.println("-->Target URI "+targetInstance.getUri());
                double similarity = measure.getSimilarity(sourceInstance, targetInstance, properties.get(0),
                        properties.get(1));
                if (similarity >= threshold) {
                    m.add(sourceInstance.getUri(), targetInstance.getUri(), similarity);
                }

            }
        }

        // in case of a db, you close the connection
        Indexer.close();
        // dictionary gets open once, during the creation of the semantic
        // similarity.
        // then, it stays open until all comparisons between instances are
        // carried out. once the comparisons are over, the dictionary must be
        // closed.
        measure.close();

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
