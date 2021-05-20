package com.example

import org.apache.spark.sql.SparkSession

class spark_code_demo_scala_1 {

  val spark = SparkSession
    .builder
    .master("local")
    .appName("SparkTest")
    .getOrCreate()

}

/**
 * @author : 焦立岩
 * @email : liyan.jiao@qzingtech.com
 * @date : 2021/5/10
 * @time : 10:28
 */
object Test {

  def main(args: Array[String]): Unit = {

    val spark = new spark_code_demo_scala_1().spark

    // RDD方式
    // args(0) 是运行函数
    val filePath = args(0)
    val rdd = spark.sparkContext.textFile(filePath)
    val lines = rdd
      .flatMap(x => x.split(" "))
      .map(x => (x,1))
      .reduceByKey((a,b) => (a+b))
      .collect()
      .toList

    println(lines)

    // DataSet方式
    import spark.implicits._
    val dataSet = spark.read
      .textFile(filePath)
      .flatMap(x => x.split(" "))
      .map(x => (x,1)).toDF("Key","value")
      .groupBy("Key")
      .count()
      .show(100)
  }
}

