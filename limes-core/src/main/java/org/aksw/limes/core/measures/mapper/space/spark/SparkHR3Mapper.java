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
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
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

    public int granularity = 4;

    public String getName() {
        return "TotalOrderBlockingMapper";
    }

    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        return null;
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
    public Dataset<Row> getMapping(Dataset<Row> source, Dataset<Row> target, String sourceVar, String targetVar, String expression,
                                   double threshold) {
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
        final int finalDimensions = property2.split("\\|").length;
        final String finalProperty1 = property1;
        final String finalProperty2 = property2;
        final String finalOperator = p.getOperator();
        final double finalThreshold = threshold;
        final int finalGranularity = granularity;

        // create input encoder
        StructType inputType = new StructType()
                .add("blockId", DataTypes.createArrayType(DataTypes.IntegerType), false)
                .add("lat", DataTypes.DoubleType, false)
                .add("long", DataTypes.DoubleType, false)
                .add("url", DataTypes.StringType, false);
        ExpressionEncoder<Row> inputEncoder = RowEncoder.apply(inputType);
        // create output encoder
        StructType outputType = new StructType()
                .add("source", DataTypes.createArrayType(DataTypes.IntegerType), false)
                .add("target", DataTypes.LongType, false)
                .add("sim", DataTypes.DoubleType, false);
        ExpressionEncoder<Row> outputEncoder = RowEncoder.apply(outputType);
        source.printSchema();
        // index source
        Dataset<Row> sourceBlocks = source
                .flatMap(row -> {
                    Instance i = new Instance(row.getString(0));
                    i.addProperty("lat", row.getString(1));
                    i.addProperty("long", row.getString(2));
                    final IBlockingModule gen = BlockingFactory.getBlockingModule(finalProperty1, finalOperator, finalThreshold, finalGranularity);
                    return gen.getAllSourceIds(i, finalProperty1).stream()
                            .flatMap(x -> gen.getBlocksToCompare(x).stream())
                            .map(x -> RowFactory.create(x.toArray(), Double.valueOf(row.getString(1)), Double.valueOf(row.getString(2)), row.getString(0))).iterator();
                }, inputEncoder);
        // index target
        Dataset<Row> targetBlocks = target
                .flatMap(row -> {
                    Instance i = new Instance(row.getString(0));
                    i.addProperty("lat", row.getString(1));
                    i.addProperty("long", row.getString(2));
                    final IBlockingModule gen = BlockingFactory.getBlockingModule(finalProperty2, finalOperator, finalThreshold, finalGranularity);
                    return gen.getAllBlockIds(i).stream().map(x -> RowFactory.create(x.toArray(), Double.valueOf(row.getString(1)), Double.valueOf(row.getString(2)), row.getString(0))).iterator();
                }, inputEncoder);
        // repartition source and target for efficient join
        sourceBlocks.repartition(sourceBlocks.col("blockId"));
        targetBlocks.repartition(targetBlocks.col("blockId"));
        // join generates link candidates and filter only keeps links with similarity >= threshold
        return sourceBlocks.joinWith(targetBlocks, sourceBlocks.col("blockId").equalTo(targetBlocks.col("blockId")))
                .map(rows -> {
                    final Instance s = new Instance(rows._1().getString(3));
                    s.addProperty("lat", rows._1().getString(1));
                    s.addProperty("long", rows._1().getString(2));
                    final Instance t = new Instance(rows._2().getString(3));
                    t.addProperty("lat", rows._2().getString(1));
                    t.addProperty("long", rows._2().getString(2));
                    ISpaceMeasure spaceMeasure = SpaceMeasureFactory.getMeasure(finalOperator, finalDimensions);
                    final double sim = spaceMeasure.getSimilarity(s, t, finalProperty1, finalProperty2);
                    return RowFactory.create(s.getUri(), t.getUri(), sim);
                }, outputEncoder)
                .filter(row -> row.getDouble(2) >= threshold)
                .toDF("source", "target", "confidence");
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
