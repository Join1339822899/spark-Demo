package com.example.ml

import org.apache.spark.ml.feature.{Word2Vec,StopWordsRemover}

import org.apache.spark.sql.{Row, SparkSession}

class spark_ml_scala_3 {
  val spark = SparkSession.builder().master("local[*]")
    .appName("Mltest").getOrCreate()

  val documentDF = spark.createDataFrame(Seq(
    "Hi I heard about Spark".split(" "),
    "I wish Java could use case classes".split(" "),
    "Logistic regression models are neat".split(" ")
  ).map(Tuple1.apply)).toDF("text")

}

/**
 * Word2Vec计算单词的分布式矢量表示。(将单词转化为矢量)
 * 分布式矢量表示被证明在许多自然语言处理应用程序中很有用，例如命名实体识别，歧义消除，解析，标记和机器翻译。
 *
 */
object Word2Vec {
  def main(args: Array[String]): Unit = {
    val ssc = new spark_ml_scala_3()
    val spark = ssc.spark
    val documentDF = ssc.documentDF

    val remover = new StopWordsRemover() //删除停用词
      .setInputCol("text")
      .setOutputCol("filtered")

    val stopWordsData = remover.transform(documentDF)//.show(false)
    stopWordsData.show(false)
    // Learn a mapping from words to Vectors.
    val word2Vec = new Word2Vec()
      .setInputCol("filtered")
      .setOutputCol("result")
      .setVectorSize(9)
      .setMinCount(0)
    val model = word2Vec.fit(stopWordsData)



    val result = model.transform(stopWordsData)
    model.findSynonyms("wish",1000).show(false)
    result.show(false)
//    result.collect().foreach { case Row(text: Seq[_], features: Vector) =>
//      println(s"Text: [${text.mkString(", ")}] => \nVector: $features\n") }
    // $example off$

    spark.stop()
  }
}