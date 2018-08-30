package org.aksw.limes.core.measures.mapper.space.spark;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.writer.CSVMappingWriter;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * @author Kevin Dreßler
 */
public class SparkEvaluation {

    private SparkSession spark = SparkSession.builder()
            .appName("LIMES HR3")
            .master("spark://spark-master:7077")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.kryo.registrator", LimesKryoRegistrator.class.getName())
            .getOrCreate();

    public void run(String cfgUrl) throws Exception {
        RDFConfigurationReader reader = new RDFConfigurationReader(cfgUrl);
        Configuration c = reader.read();
        Dataset<Instance> sourceDS = readInstancesFromCSV(c.getSourceInfo().getEndpoint());
        Dataset<Instance> targetDS = readInstancesFromCSV(c.getTargetInfo().getEndpoint());
        String measureExpr = c.getMetricExpression();
        double threshold = c.getAcceptanceThreshold();
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        FileSystem fs = FileSystem.get(configuration);
        Path fileName = new Path("hdfs://namenode:8020/user/admin/eval.csv");
        try {
            if (fs.exists(fileName)) {
                fs.delete(fileName, true);
            }

            FSDataOutputStream fin = fs.create(fileName);
            fin.writeUTF("Iteration\tSpark\n");
            SparkHR3Mapper sparkHR3Mapper = new SparkHR3Mapper();
            for(int i = 0; i < 10; i++){
                long start = System.currentTimeMillis();
                AMapping links = sparkHR3Mapper.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, threshold);
                long finish = System.currentTimeMillis();
                long time = finish - start;
                fin.writeUTF(i + "\t" + time + "\n");
//                CSVMappingWriter linkWriter = new CSVMappingWriter();
//                linkWriter.write(links, "SparkHR3links.csv");
            }
            fin.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception {
        new SparkEvaluation().run(args[0]);

    }

    private Dataset<Instance> readInstancesFromCSV(String path){
        Dataset<Row> ds = spark.read()
                .format("csv")
                .option("header", "true")
                .option("mode", "DROPMALFORMED")
                .load(path);
        return ds.map(line -> {
            Instance i = new Instance(line.getString(0));
            i.addProperty("lat",line.getString(1));
            i.addProperty("long",line.getString(2));
            return i;
        }, Encoders.kryo(Instance.class));
    }

}
