package com.example;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class spark_structured_streaming_demo_java_1 {
    private static SparkSession getSparkSeddion(){
        SparkSession sc = SparkSession
                .builder()
                .master("local")
                .appName("StructuredNetworkWordCount")
                .getOrCreate();

        return sc;
    }

    public static void main(String[] args) {
        SparkSession ss = getSparkSeddion();
        Dataset<Row> load = ss.readStream()
                .format("socket")
                .option("host", "localhost")
                .option("port", 9999).load();

        load.printSchema();

        // Split the lines into words
        Dataset<String> words = load
                .as(Encoders.STRING())
                .flatMap((FlatMapFunction<String, String>) x -> Arrays.asList(x.split(" ")).iterator(), Encoders.STRING());

        // Generate running word count
        Dataset<Row> wordCounts = words.groupBy("value").count();

        // Start running the query that prints the running counts to the console
        StreamingQuery query = null;

        try {
            query = wordCounts.writeStream()
                    .outputMode("complete")
                    .format("console")
                    .start();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        try {
            query.awaitTermination();
        } catch (StreamingQueryException e) {
            e.printStackTrace();
        }

    }
}
