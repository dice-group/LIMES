package org.aksw.limes.core.measures.mapper.space.spark;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.writer.CSVMappingWriter;
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

    private static SparkSession spark = SparkSession.builder()
            .appName("Example")
            .master("local[*]")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.kryo.registrator", LimesKryoRegistrator.class.getName())
            .getOrCreate();

    public static void main(String[] args) throws Exception {
        RDFConfigurationReader reader = new RDFConfigurationReader(args[0]);
        Configuration c = reader.read();
        Dataset<Instance> sourceDS = readInstancesFromCSV(c.getSourceInfo().getEndpoint());
        Dataset<Instance> targetDS = readInstancesFromCSV(c.getTargetInfo().getEndpoint());
        String measureExpr = c.getMetricExpression();
        double threshold = c.getAcceptanceThreshold();
        PrintWriter resWriter = new PrintWriter(new FileOutputStream("SparkHR3Eval.csv"));
        resWriter.write("Iteration\tSpark\n");
        resWriter.close();
        SparkHR3Mapper sparkHR3Mapper = new SparkHR3Mapper();
        for(int i = 0; i < 10; i++){
            resWriter = new PrintWriter(new FileOutputStream("SparkHR3Eval.csv", true));
            long start = System.currentTimeMillis();
            AMapping links = sparkHR3Mapper.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, threshold);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            resWriter.write(i + "\t" + time + "\n");
            resWriter.close();
            CSVMappingWriter linkWriter = new CSVMappingWriter();
            linkWriter.write(links, "SparkHR3links.csv");
        }
    }

    private static Dataset<Instance> readInstancesFromCSV(String path){
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
