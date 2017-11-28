package org.aksw.limes.core.measures.mapper.space.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Dreßler
 */
public class SparkEvaluation {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("SparkMe Application")
                .setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SparkSession ses = new SparkSession(JavaSparkContext.toSparkContext(sc));
        try {
            TimeUnit.MINUTES.sleep(2);
        } catch (Exception e) {

        } finally {
            ses.stop();
            sc.stop();
        }
    }

}
