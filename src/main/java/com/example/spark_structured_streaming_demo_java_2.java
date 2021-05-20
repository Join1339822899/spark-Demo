package com.example;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import scala.Tuple2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class spark_structured_streaming_demo_java_2 {
    private static SparkSession getSparkSeddion(){
        SparkSession sc = SparkSession
                .builder()
                .master("local")
                .appName("StructuredNetworkWordCount")
                .getOrCreate();

        return sc;
    }

    public static void main(String[] args) throws Exception {
        SparkSession spark = getSparkSeddion();

        // Create DataFrame representing the stream of input lines from connection to host:port
        Dataset<Row> lines = spark
                .readStream()
                .format("socket")
                .option("host", "localhost")
                .option("port", "9999")
                .option("includeTimestamp", true)
                .load();

        // Split the lines into words, retaining timestamps
        Dataset<Row> words = lines
                .as(Encoders.tuple(Encoders.STRING(), Encoders.TIMESTAMP()))
                .flatMap((FlatMapFunction<Tuple2<String, Timestamp>, Tuple2<String, Timestamp>>) t -> {
                            List<Tuple2<String, Timestamp>> result = new ArrayList<>();
                            for (String word : t._1.split(" ")) {
                                result.add(new Tuple2<>(word, t._2));
                            }
                            return result.iterator();
                        },
                        Encoders.tuple(Encoders.STRING(), Encoders.TIMESTAMP())
                ).toDF("word", "timestamp");

        // Group the data by window and word and compute the count of each group
        Dataset<Row> windowedCounts = words.withWatermark("timestamp", "30 seconds").groupBy(
                functions.window(words.col("timestamp"), "10 seconds", "10 seconds"),
                words.col("word")
        ).count().orderBy("window");

        // Start running the query that prints the windowed word counts to the console
        StreamingQuery query = windowedCounts.writeStream()
                .outputMode("complete")
                .format("console")
                .option("truncate", "false")
                .start();

        query.awaitTermination();
    }}
