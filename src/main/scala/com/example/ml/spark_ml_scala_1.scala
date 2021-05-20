package com.example.ml

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature._
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.{Row, SparkSession}

class spark_ml_scala_1 {
  val spark = SparkSession.builder().master("local[*]")
    .appName("Mltest").getOrCreate()

  val seq = Seq(
    (0L,"a b c d e arde",2.0)
    ,(1L,"b d arde",2.0)
    ,(2L,"spark f g h",0.0)
    ,(3L,"hadoop mapreduce",0.0),
    (8L,"arde",2.0)
  )
  // Prepare test documents, which are unlabeled (id, text) tuples.
  val test = spark.createDataFrame(Seq(
    (4L, "spark i j k"),
    (5L, "l m n arde"),
    (6L, "arde hadoop join spark"),
    (7L, "apache hadoop arde")
  )).toDF("id", "text")

}

object Spark_ML_1{
  def main (args: Array[String] ): Unit = {
    val spark_ml = new spark_ml_scala_1()
    val spark = spark_ml.spark
    val seq = spark_ml.seq
    val test = spark_ml.test

    val training = spark.createDataFrame(seq)
      .toDF("id","text","label")   //构建测试数据集

    // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
    val tokenizer = new org.apache.spark.ml.feature.Tokenizer()
      .setInputCol("text")
      .setOutputCol("words")  //定义转换器

    val hashingTF = new HashingTF().setNumFeatures(1000).setInputCol(tokenizer.getOutputCol)
      .setOutputCol("features")  //定义转换器，将单词转换为特征向量

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.01) //定义评估器


    val pipLine = new Pipeline().setStages(Array(tokenizer,hashingTF,lr)); //机器学习流水线，本质上是一个评估器，需要调用fit()方法
    val model = pipLine.fit(training);//私用fit()方法进行训练，得到一个模型，就是PipLineModel

    model.transform(test) //注入需要处理的文本
      .select("id", "text", "probability", "prediction").show(false)
    //  .collect()
    //  .foreach { case Row(id: Long, text: String, prob: Vector, prediction: Double) =>
    //    println(s"($id, $text) --> prob=$prob, prediction=$prediction")
    //  } //得到处理的结果 prod表示为0或为1的几率 prediction为结果
    // $example off$

    spark.stop()
  }
}
