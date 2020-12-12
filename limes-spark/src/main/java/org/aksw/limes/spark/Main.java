package org.aksw.limes.spark;

import com.google.common.collect.Lists;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

/**
 * @author Kevin Dreßler
 */
public class Main {

    public static int partitions = 768;

    private final SparkSession spark = SparkSession.builder()
            .appName("LIMES HR3")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.dynamicAllocation.enabled", false)
            .getOrCreate();

    private static final StructType inType = new StructType()
            .add("url", DataTypes.StringType, false)
            .add("data", DataTypes.StringType, false);

    public void run(String sourceDatasetPath, String targetDatasetPath, double threshold, String evalUrl, String outputUrl, FileSystem fs) throws Exception {
        Dataset<Row> sourceDS = readInstancesFromCSV(sourceDatasetPath);//.cache();
        Dataset<Row> targetDS = readInstancesFromCSV(targetDatasetPath);//.cache();
        SparkHR3Mapper sparkHR3Mapper = new SparkHR3Mapper();
        long init = System.currentTimeMillis();
//        sparkHR3Mapper.getMapping(spark.createDataFrame(Lists.newArrayList(RowFactory.create("test","17.3,37.2")), inType), spark.createDataFrame(Lists.newArrayList(RowFactory.create("test","17.3,37.2")), inType), threshold, 4).count();
        init = System.currentTimeMillis() - init;
        long sizeA = sourceDS.count();
        long sizeB = targetDS.count();
        partitions *= Math.pow(10,Math.ceil(Math.max(0, Math.log10(Math.max(sizeA, sizeB))-6)));
        if (sizeA > sizeB) {
            Dataset<Row> tmp = sourceDS;
            sourceDS = targetDS;
            targetDS = tmp;
        }
        Path evalPath = new Path(evalUrl);
        Path linksPath = new Path(outputUrl);
        try {
            FSDataOutputStream fin = fs.create(evalPath, true);
            fin.writeUTF("i\tt_comp\tt_write\tinit\tn_links\n");

            for (int i = 0; i < 1; i++) {
                if (fs.exists(linksPath)) {
                    fs.delete(linksPath, true);
                }
                long start = System.currentTimeMillis();
                Dataset<Row> mapping = sparkHR3Mapper
                        .getMapping(sourceDS, targetDS, threshold, 1)
                        ;//.cache();
                long count = mapping.count();
                long comp = System.currentTimeMillis();
                if (sizeA > sizeB) {
                    mapping = mapping.map((MapFunction<Row, Row>) r ->
                            RowFactory.create(r.get(1), r.get(0), r.get(2)), SparkHR3Mapper.outputEncoder);
                }
                mapping.write().csv(outputUrl);
                long finish = System.currentTimeMillis();
                fin.writeUTF(i + "\t" + (comp - start) + "\t" + (finish - comp) + "\t" + init + "\t" + count + "\n");
                mapping.unpersist();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            fs.close();
//            sourceDS.unpersist();
//            targetDS.unpersist();
        }
    }
    // sourceDatasetPath, targetDatasetPath, threshold, evaluationOutputPath, linksOutputPath
    public static void main(String[] args) throws Exception {
        final GenericOptionsParser optionsParser = new GenericOptionsParser(args);
        final String[] posArgs = optionsParser.getRemainingArgs();
        final org.apache.hadoop.conf.Configuration conf = optionsParser.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        String sourceDatasetPath = posArgs[0];
        String targetDatasetPath = posArgs[1];
        double threshold = Double.parseDouble(posArgs[2]);
        String evaluationOutputPath = posArgs[3];
        String linksOutputPath = posArgs[4];
        new Main().run(sourceDatasetPath, targetDatasetPath, threshold, evaluationOutputPath, linksOutputPath, fs);
    }

    private Dataset<Row> readInstancesFromCSV(String path) {
        Dataset<Row> load = spark.read()
                .format("csv")
                .option("delimiter", ";")
                .option("header", "false")
                .option("mode", "DROPMALFORMED")
                .load(path);
        return load;
    }

}
