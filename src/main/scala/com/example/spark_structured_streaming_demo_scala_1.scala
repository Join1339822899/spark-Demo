package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

class spark_structured_streaming_demo_scala_1 {


  val spark = SparkSession
    .builder
    .appName("StructuredNetworkWordCount")
    .master("local")
    .getOrCreate()


}

object Structured_Streaming_Test {
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
      .load()

    socketDF.isStreaming    // Returns True for DataFrames that have streaming sources
    socketDF.printSchema

    import spark.implicits._
    val DataSetStreaming = socketDF.as[String]
      .flatMap(x=>x.split(","))
      .map((_,1))
      .groupBy("_1")
      .count()
      .toDF("Key","Value")

    DataSetStreaming.writeStream.format("console").outputMode(OutputMode.Complete())
      .start().awaitTermination()

//    //支持 orc json csv
//    DataSetStreaming.writeStream.format("json")
//      .outputMode("complete")
//      .option("path","src/main/document/Structured_Streaming.csv")
//      .option("checkpointLocation", "src/main/document")
//      .start().awaitTermination()


  }
}