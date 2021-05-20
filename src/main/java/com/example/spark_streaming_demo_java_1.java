package com.example;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.ReceiverInputDStream;
import scala.Tuple2;

import java.util.Arrays;

public class spark_streaming_demo_java_1 {
    private static SparkSession getSparkSeddion(){
        SparkSession sc = SparkSession
                .builder()
                .master("local[*]")
                .appName("NetworkWordCount")
                .getOrCreate();

        return sc;
    }

    public static void main(String[] args) {
        SparkSession ss =  getSparkSeddion();
        SparkContext sparkContext = ss.sparkContext();
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkContext);
        JavaStreamingContext jssc = new JavaStreamingContext(javaSparkContext, Durations.seconds(1));
        // Create a DStream that will connect to hostname:port, like localhost:9999
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);
        // Split each line into words
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator());
        // Count each word in each batch
        JavaPairDStream<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));
        JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey((i1, i2) -> i1 + i2);

        // Print the first ten elements of each RDD generated in this DStream to the console
        wordCounts.print();
        jssc.start();              // Start the computation
        try {
            jssc.awaitTermination();   // Wait for the computation to terminate
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
