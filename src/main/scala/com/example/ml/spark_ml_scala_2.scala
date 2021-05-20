package com.example.ml

import org.apache.spark.ml.feature._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}

import java.util

class spark_ml_scala_2 {
  val spark = SparkSession.builder().master("local[*]")
    .appName("Mltest").getOrCreate()

  val schema = StructType(List(
    //StructField("label", DoubleType, true),
    StructField("sentence", StringType, true)
  ))

  val dataList = new util.ArrayList[Row]()
  dataList.add(Row("Hi I heard about Spark"))
  dataList.add(Row("I wish Java could use case classes"))
  dataList.add(Row("Logistic regression models are neat"))

  // $example on$
  val sentenceData = spark.createDataFrame(dataList,schema
  )

}

/**
 * 术语频率逆文档频率（TF-IDF）是一种特征向量化方法，广泛用于文本挖掘中，以反映术语对语料库中文档的重要性。
 * 使用将每个句子分成单词Tokenizer。
 * 对于每个句子（单词袋），我们用于HashingTF将句子散列为特征向量。
 * 用IDF来重新缩放特征向量；
 */
object Spark_ML_TFIDF_2{
  def main(args: Array[String]): Unit = {
    val ssc = new spark_ml_scala_2()
    val spark = ssc.spark

    //分词
    val tokenizer = new Tokenizer().setInputCol("sentence")
      .setOutputCol("words")

    val senencedata = ssc.sentenceData
    val wordsData = tokenizer.transform(senencedata)

    wordsData.show(false)//查看分词的结果

    val hashingTF = new HashingTF().setInputCol("words")
      .setOutputCol("rawFeatures")
      .setNumFeatures(99999)

    val featurizedData = hashingTF.transform(wordsData)

    featurizedData.show(false) //输出特征变量

    val idf = new IDF().setInputCol("rawFeatures")
      .setOutputCol("Features")

    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)

    rescaledData.select("Features").show(false)

    spark.stop()
  }

}
