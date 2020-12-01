//package org.aksw.limes.core.measures.mapper.space.spark;
//
//import org.aksw.limes.core.io.cache.Instance;
//import org.aksw.limes.core.io.config.Configuration;
//import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.util.GenericOptionsParser;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//
///**
// * @author Kevin Dreßler
// */
//public class SparkEvaluation {
//
//    private SparkSession spark = SparkSession.builder()
//            .appName("LIMES HR3")
//            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//            .config("spark.kryo.registrator", LimesKryoRegistrator.class.getName())
//            .config("spark.dynamicAllocation.enabled", false)
//            .getOrCreate();
//
//    public void run(String cfgUrl, String evalUrl, String outputUrl, FileSystem fs) throws Exception {
//        RDFConfigurationReader reader = new RDFConfigurationReader(cfgUrl);
//        Configuration c = reader.read();
//        Dataset<Row> sourceDS = readInstancesFromCSV(c.getSourceInfo().getEndpoint());
//        Dataset<Row> targetDS = readInstancesFromCSV(c.getTargetInfo().getEndpoint());
//        String measureExpr = c.getMetricExpression();
//        double threshold = c.getAcceptanceThreshold();
//        Path evalPath = new Path(evalUrl);
//        Path linksPath = new Path(outputUrl);
//        try {
//            FSDataOutputStream fin = fs.create(evalPath, true);
//            fin.writeUTF("Iteration\tComputation\tOutput\n");
//            SparkHR3Mapper sparkHR3Mapper = new SparkHR3Mapper();
//            for (int i = 0; i < 10; i++) {
//                if (fs.exists(linksPath)) {
//                    fs.delete(linksPath, true);
//                }
//                long start = System.currentTimeMillis();
//                Dataset<Row> mapping = sparkHR3Mapper.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, threshold);
////                long count = mapping.count();
////                System.out.println("Generated " + count + " links!");
//                long comp = System.currentTimeMillis();
//                mapping.write().csv(outputUrl);
//                long finish = System.currentTimeMillis();
//                fin.writeUTF(i + "\t" + (comp - start) + "\t" + (finish - comp) + "\n");
//            }
//            fin.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        fs.close();
//    }
//
//
//    public static void main(String[] args) throws Exception {
//        final GenericOptionsParser optionsParser = new GenericOptionsParser(args);
//        final String[] posArgs = optionsParser.getRemainingArgs();
//        final org.apache.hadoop.conf.Configuration conf = optionsParser.getConfiguration();
//        FileSystem fs = FileSystem.get(conf);
//        new SparkEvaluation().run(posArgs[0], posArgs[1], posArgs[2], fs);
//
//    }
//
//    private Dataset<Row> readInstancesFromCSV(String path) {
//        Dataset<Row> ds = spark.read()
//                .format("csv")
//                .option("header", "true")
//                .option("mode", "DROPMALFORMED")
//                .load(path);
//        return ds;
////            Instance i = new Instance(line.getString(0));
////            i.addProperty("lat", line.getString(1));
////            i.addProperty("long", line.getString(2));
////            return i;
////        }, Encoders.kryo(Instance.class));
//    }
//
//}
