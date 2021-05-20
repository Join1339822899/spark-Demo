package com.example.ml

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}

class spark_ml_scala_4 {
  val spark = SparkSession.builder().master("local[*]")
    .appName("Mltest").getOrCreate()

  val df = spark.createDataFrame(Seq(
    (0, Array("a", "b", "c","e","f")),
    (1, Array("a", "b", "b", "c", "a","e","e"))
  )).toDF("id", "words")
}

/**
 * 在拟合过程中，CountVectorizer将选择vocabSize整个语料库中按词频排列的前几个词。
 * 可选参数minDF还通过指定一个单词必须出现在词汇表中的最小数量（如果小于1.0，则为小数）来影响拟合过程。
 * 另一个可选的二进制切换参数控制输出向量。如果将其设置为true，则所有非零计数都将设置为1。
 */
object SPark_Ml_CountVectorizer{
  def main(args: Array[String]): Unit = {
    val ssc = new spark_ml_scala_4
    val spark = ssc.spark
    val df = ssc.df

    // fit a CountVectorizerModel from the corpus
    val cvModel: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .setVocabSize(10)
      .setMinDF(2)
      .fit(df)

    // alternatively, define CountVectorizerModel with a-priori vocabulary
    val cvm = new CountVectorizerModel(Array("a", "b", "c"))
      .setInputCol("words")
      .setOutputCol("features")

    cvModel.transform(df).show(false)
  }
}