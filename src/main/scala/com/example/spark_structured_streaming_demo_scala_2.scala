package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.window
import org.apache.spark.sql.streaming.{OutputMode, Trigger}

import java.sql.Timestamp

class spark_structured_streaming_demo_scala_2 {
  val spark = SparkSession
    .builder
    .appName("StructuredNetworkWordCount")
    .master("local")
    .getOrCreate()
}

object Structured_Streaming_Test_Windows{
  def main(args: Array[String]): Unit = {
    val spark = new spark_structured_streaming_demo_scala_1().spark;
    // Create DataFrame representing the stream of input lines from connection to localhost:9999
    // winwods : nc -l -p 9999
    // liunx : nc -l -p 9999
    // Read text from socket
    val socketDF = spark
      .readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9999)
      .option("includeTimestamp", true)
      .load()

    import spark.implicits._
    socketDF.printSchema()

    val words = socketDF.as[(String, Timestamp)]
      .flatMap(line =>line._1.split(" ")
        .map(word => (word, line._2))).toDF("word", "timestamp")

    val windowedCounts = words.withWatermark("timestamp", "30 seconds")  //制定事务时间字段，设置水位时间，只计算最新的时间到水位线的数据
      .groupBy(
        window($"timestamp", "10 seconds", "10 seconds","0 seconds")
        , $"word") //按时间窗口与文本内容进行分组聚合，
      // timeColumn 分组的时间字段，
      // 指的是上一个DataFrame里的时间字段，
      // windowDuration 窗口持续时间，
      // slideDuration 窗口滑动时间,
      // startTime 窗口开始时间

      .count()//.sort("window")

    windowedCounts.printSchema()

    windowedCounts.
      writeStream
      .outputMode(OutputMode.Update())
      .format("console")
      .trigger(Trigger.ProcessingTime(0))  //执行查询的时间，为0则收到数据立即触发
      .option("truncate", "false").start()
      .awaitTermination()
  }
}