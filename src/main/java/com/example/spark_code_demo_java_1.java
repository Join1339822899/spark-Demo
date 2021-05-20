package com.example;

import com.example.pojo.TestTxT;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

class spark_code_demo_java_1 {

    private static SparkSession getSparkSeddion(){
        SparkSession sc = SparkSession
                .builder()
                .master("local")
                .appName("SparkTest")
                .getOrCreate();

        return sc;
    }

    private static JavaSparkContext getSparkContext(){
        SparkConf conf = new SparkConf().setAppName("SparkTest").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
    }


    public void Rdd_Demo(String path){
//      JavaSparkContext sparkContext1 = getSparkContext();  //两种构建SparkContext的方法，推荐使用下面这一种
        JavaSparkContext sparkContext1 =JavaSparkContext.fromSparkContext(getSparkSeddion().sparkContext());
        JavaRDD<String> lines = sparkContext1.textFile(path);
        JavaRDD<String> flatJavaRDD = lines.flatMap(rdd -> Arrays.asList(rdd.split(" ")).iterator());
        JavaPairRDD<String, Integer> KVJavaPairRDD = flatJavaRDD.mapToPair(rdd -> new Tuple2<>(rdd, 1));
        JavaPairRDD<String, Integer> RBKJavaPairRDD = KVJavaPairRDD.reduceByKey((a, b) -> a + b);
        List<Tuple2<String, Integer>> collect = RBKJavaPairRDD.collect();
        System.out.println(collect);
    }

    public void DataSet(String path){
        SparkSession sparkSeddion = getSparkSeddion();
        Dataset<String> stringDataset = sparkSeddion.read().textFile(path);
        JavaRDD<String> stringJavaRDD = stringDataset.javaRDD() //Dataset转换为RDD
                .flatMap(rdd -> Arrays.asList(rdd.split(" ")).iterator());
        JavaPairRDD<String, Integer> KVJavaPairRDD = stringJavaRDD.mapToPair(rdd -> new Tuple2<>(rdd, 1));
        JavaRDD<TestTxT> map = KVJavaPairRDD.map(rdd ->{
            TestTxT testTxT = new TestTxT();
            testTxT.setKey(rdd._1);
            testTxT.setValue(rdd._2);
            return testTxT;
        });

        Dataset<Row> textDF = sparkSeddion.createDataFrame(map, TestTxT.class); //RDD转换为Dataset

        textDF.groupBy("key").count().show(100);
    }

    public static void main(String[] args) {
        new spark_code_demo_java_1().Rdd_Demo(args[0]);
        new spark_code_demo_java_1().DataSet(args[0]);
    }
}
