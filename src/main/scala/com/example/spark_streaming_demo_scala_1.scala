package com.example

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Spark_Streaming_Test{
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("NetworkWordCount")
      .master("local[*]")
      .getOrCreate()
    val sc = spark.sparkContext
    val scc = new StreamingContext(sc,Seconds(1))

    val lines = scc.socketTextStream("localhost", 9999)
    // Split each line into words
    val words = lines.flatMap(_.split(" "))
    import org.apache.spark.streaming.StreamingContext._ // not necessary since Spark 1.3
    // Count each word in each batch
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.foreachRDD(
      rdd => rdd.map(x => (x,1)).collect()
    )
    wordCounts.print()
    scc.start()             // Start the computation
    scc.awaitTermination()  // Wait for the computation to terminate

  }
}