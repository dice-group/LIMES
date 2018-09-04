package org.aksw.limes.core.measures.mapper.space.spark;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @author Kevin Dreßler
 */
public class SparkEvaluation {

    private SparkSession spark = SparkSession.builder()
            .appName("LIMES HR3")
            .master("spark://172.18.160.16:3090")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.kryo.registrator", LimesKryoRegistrator.class.getName())
            .config("spark.cores.max", 90)
            .config("spark.dynamicAllocation.enabled", false)
            .getOrCreate();

    public void run(String cfgUrl, String evalUrl, String outputUrl, int n) throws Exception {
        RDFConfigurationReader reader = new RDFConfigurationReader(cfgUrl);
        Configuration c = reader.read();
        Dataset<Instance> sourceDS = readInstancesFromCSV(c.getSourceInfo().getEndpoint(), n);
        Dataset<Instance> targetDS = readInstancesFromCSV(c.getTargetInfo().getEndpoint(), n);
        String measureExpr = c.getMetricExpression();
        double threshold = c.getAcceptanceThreshold();
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
        FileSystem fs = FileSystem.get(configuration);
        Path evalPath = new Path(evalUrl);
        Path linksPath = new Path(outputUrl);
        try {
            FSDataOutputStream fin = fs.create(evalPath, true);
            fin.writeUTF("Iteration\tComputation\tOutput\n");
            SparkHR3Mapper sparkHR3Mapper = new SparkHR3Mapper();
            for (int i = 0; i < 10; i++) {
                if (fs.exists(linksPath)) {
                    fs.delete(linksPath, true);
                }
                long start = System.currentTimeMillis();
                Dataset<Row> mapping = sparkHR3Mapper.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, threshold);
                long count = mapping.count();
                System.out.println("Generated " + count + " links!");
                long comp = System.currentTimeMillis();
                mapping.write().csv(outputUrl);
                long finish = System.currentTimeMillis();
                fin.writeUTF(i + "\t" + (comp - start) + "\t" + (finish - comp) + "\n");
            }
            fin.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fs.close();
    }


    public static void main(String[] args) throws Exception {
        new SparkEvaluation().run(args[0], args[1], args[2], Integer.valueOf(args[3]));

    }

    private Dataset<Instance> readInstancesFromCSV(String path, int n) {
        Dataset<Row> ds = spark.read()
                .format("csv")
                .option("header", "true")
                .option("mode", "DROPMALFORMED")
                .load(path)
                .limit(n);
        return ds.map(line -> {
            Instance i = new Instance(line.getString(0));
            i.addProperty("lat", line.getString(1));
            i.addProperty("long", line.getString(2));
            return i;
        }, Encoders.kryo(Instance.class));
    }

}
