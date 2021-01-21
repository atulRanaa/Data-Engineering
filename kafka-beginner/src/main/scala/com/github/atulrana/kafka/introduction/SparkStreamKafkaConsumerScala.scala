package com.github.atulrana.kafka.introduction

import org.apache.spark.sql.{Dataset, Row, SparkSession}

object SparkStreamKafkaConsumerScala {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("JavaKafkaConnect").master("local").getOrCreate()

    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "topic1")
      .load()

    df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").show(1000, truncate = false)
  }
}
