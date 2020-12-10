//package org.aksw.limes.spark;
//
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.api.java.function.ForeachFunction;
//import org.apache.spark.api.java.function.MapFunction;
//import org.apache.spark.sql.*;
//import org.apache.spark.sql.types.DataTypes;
//import org.apache.spark.sql.types.StructType;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// *
// */
//public class SparkHR3MapperTest {
//    @Before
//    public void setUp(){
//        SparkSession.builder().master("local[4]").getOrCreate();
//    }
//
//    @Test
//    public void testSpark() {
//        SparkSession spark = SparkSession.active();
//        final JavaSparkContext sc = JavaSparkContext.fromSparkContext(spark.sparkContext());
//        sc.setLogLevel("ERROR");
//        List<Row> rows = new ArrayList<>();
//        rows.add(RowFactory.create("one", 123987L));
//        rows.add(RowFactory.create("two", 4923849L));
//        rows.add(RowFactory.create("three", 9338L));
//        rows.add(RowFactory.create("four", 9338L));
//        StructType t = new StructType()
//                .add("name", DataTypes.StringType, false)
//                .add("number", DataTypes.LongType, false);
//        Dataset<Row> df = spark.createDataFrame(rows, t);
//        Set<Long> s = new HashSet<>();
//        df.foreach((ForeachFunction<Row>) r -> {s.add(r.getLong(1)); System.out.println(s.size());});
//
//        df.map((MapFunction<Row, Long>) r -> r.getLong(1), Encoders.LONG())
//                .distinct()
//                .collectAsList().forEach(System.out::println);
//
//    }
//}