package com.github.atulrana.kafka.introduction;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkStreamKafkaConsumer {
    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("JavaKafkaConnect")
                .master("local")
                .getOrCreate();

        Dataset<Row> df = spark
                .read()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "topic1")
                .load();

        df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").show(1000, false);
    }
}
