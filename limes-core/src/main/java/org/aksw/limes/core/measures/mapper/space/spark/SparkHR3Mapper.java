package org.aksw.limes.core.measures.mapper.space.spark;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.space.blocking.BlockingFactory;
import org.aksw.limes.core.measures.mapper.space.blocking.IBlockingModule;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

/**
 * Uses metric spaces to create blocks.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
@SuppressWarnings("Duplicates")
public class SparkHR3Mapper extends AMapper {

    private static final Logger logger = LoggerFactory.getLogger(SparkHR3Mapper.class);

    public static class HR3Block {

        private List<Integer> id;

        public static Tuple2<HR3Block, Instance> createTuple(List<Integer> blockId, Instance instance) {
            HR3Block hr3Block = new HR3Block();
            hr3Block.id = blockId;
            return new Tuple2<>(hr3Block, instance);
        }

        public List<Integer> getId() {
            return id;
        }

        public void setId(List<Integer> id) {
            this.id = id;
        }
    }

    public int granularity = 4;

    public String getName() {
        return "TotalOrderBlockingMapper";
    }

    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        SparkSession spark = SparkSession.builder().getOrCreate();
        return getMapping(
                spark.createDataset(source.getAllInstances(), Encoders.kryo(Instance.class)),
                spark.createDataset(source.getAllInstances(), Encoders.kryo(Instance.class)),
                sourceVar, targetVar, expression, threshold);
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
    public AMapping getMapping(Dataset<Instance> source, Dataset<Instance> target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        AMapping mapping = MappingFactory.createDefaultMapping();
        // 0. get properties
        String property1, property2;
        // get property labels
        Parser p = new Parser(expression, threshold);
        // get first property label
        String term1 = p.getLeftTerm();
        if (term1.contains(".")) {
            String split[] = term1.split("\\.");
            property1 = split[1];
            if (split.length >= 2)
                for (int part = 2; part < split.length; part++)
                    property1 += "." + split[part];
        } else {
            property1 = term1;
        }
        // get second property label
        String term2 = p.getRightTerm();
        if (term2.contains(".")) {
            String split[] = term2.split("\\.");
            property2 = split[1];
            if (split.length >= 2)
                for (int part = 2; part < split.length; part++)
                    property2 += "." + split[part];
        } else {
            property2 = term2;
        }
        // get number of dimensions we are dealing with
        int dimensions = property2.split("\\|").length;
        IBlockingModule generator = BlockingFactory.getBlockingModule(property2, p.getOperator(), threshold,
                granularity);
        // initialize the measure for similarity computation
        ISpaceMeasure spaceMeasure = SpaceMeasureFactory.getMeasure(p.getOperator(), dimensions);
        KryoSerializationWrapper<ISpaceMeasure> measure = new KryoSerializationWrapper<>(spaceMeasure, spaceMeasure.getClass());
        final String finalProperty1 = property1;
        final String finalProperty2 = property2;
        KryoSerializationWrapper<IBlockingModule> blockingWrapper = new KryoSerializationWrapper<>(generator, generator.getClass());
        Dataset<Tuple2<HR3Block, Instance>> sourceBlocks = source
                .flatMap(i -> {
                    final IBlockingModule gen = blockingWrapper.get();
                    return gen.getAllSourceIds(i, finalProperty1).stream()
                            .flatMap(x -> gen.getBlocksToCompare(x).stream())
                            .map(x -> HR3Block.createTuple(x, i)).iterator();
                }, Encoders.tuple(Encoders.bean(HR3Block.class),Encoders.kryo(Instance.class)));
        Dataset<Tuple2<HR3Block, Instance>> targetBlocks = target
                .flatMap(i -> {
                    final IBlockingModule gen = blockingWrapper.get();
                    return gen.getAllBlockIds(i).stream().map(x -> HR3Block.createTuple(x, i)).iterator();
                }, Encoders.tuple(Encoders.bean(HR3Block.class),Encoders.kryo(Instance.class)));
        sourceBlocks.repartition(sourceBlocks.col("_1.id"));
        targetBlocks.repartition(targetBlocks.col("_1.id"));
        sourceBlocks.joinWith(targetBlocks, sourceBlocks.col("_1.id").equalTo(targetBlocks.col("_1.id")))
                .map(tuple -> {
                    final Instance s = tuple._1()._2();
                    final Instance t = tuple._2()._2();
                    final double sim = measure.get().getSimilarity(s, t, finalProperty1, finalProperty2);
                    return new Tuple3<>(s.getUri(), t.getUri(), sim);
                }, Encoders.tuple(Encoders.STRING(), Encoders.STRING(), Encoders.DOUBLE()))
                .write().csv("hdfs://namenode:8020/user/admin/links.csv");
//                .collectAsList()
//                .forEach(t -> mapping.add(t._1(), t._2(), t._3()));
        return mapping;
    }

    // need to change this
    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
        if (language.equals(Language.DE)) {
            // error = 667.22
            return 16.27 + 5.1 * sourceSize + 4.9 * targetSize - 23.44 * threshold;
        } else {
            // error = 5.45
            return 200 + 0.005 * (sourceSize + targetSize) - 56.4 * threshold;
        }
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
        if (language.equals(Language.DE)) {
            // error = 667.22
            return 2333 + 0.14 * sourceSize + 0.14 * targetSize - 3905 * threshold;
        } else {
            // error = 5.45
            return 0.006 * (sourceSize + targetSize) - 134.2 * threshold;
        }
    }
}
