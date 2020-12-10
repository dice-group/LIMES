package org.aksw.limes.spark;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @author Kevin Dreßler
 */
public class Main {

    private SparkSession spark = SparkSession.builder()
            .appName("LIMES HR3")
//            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//            .confg("spark.kryo.registrator", LimesKryoRegistrator.class.getName())
            .config("spark.dynamicAllocation.enabled", false)
            .getOrCreate();

    public void run(String sourceDatasetPath, String targetDatasetPath, double threshold, String evalUrl, String outputUrl, FileSystem fs) throws Exception {
        Dataset<Row> sourceDS = readInstancesFromCSV(sourceDatasetPath).cache();
        Dataset<Row> targetDS = readInstancesFromCSV(targetDatasetPath).cache();
        sourceDS.count();
        targetDS.count();
        Path evalPath = new Path(evalUrl);
        Path linksPath = new Path(outputUrl);
        try {
            FSDataOutputStream fin = fs.create(evalPath, true);
            fin.writeUTF("Iteration\tComputation\tOutput\n");
            SparkHR3Mapper sparkHR3Mapper = new SparkHR3Mapper(); //@todo make this dynamic
            for (int i = 0; i < 10; i++) {
                if (fs.exists(linksPath)) {
                    fs.delete(linksPath, true);
                }
                long start = System.currentTimeMillis();
                Dataset<Row> mapping = sparkHR3Mapper
                        .getMapping(sourceDS, targetDS, threshold, 4)
                        .cache();
                long count = mapping.count();
                long comp = System.currentTimeMillis();
                mapping.write().csv(outputUrl);
                long finish = System.currentTimeMillis();
                fin.writeUTF(i + "\t" + (comp - start) + "\t" + (finish - comp) + "\t" + count + "\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            fs.close();
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
        Dataset<Row> ds = spark.read()
                .format("csv")
                .option("delimiter", ";")
                .option("header", "true")
                .option("mode", "DROPMALFORMED")
                .load(path);
        return ds;
    }

}
