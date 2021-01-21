package com.github.atulrana.kafka.introduction

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object ScalaDirectKafkaConsumer {

  @throws[InterruptedException]
  def main(args: Array[String]): Unit = {

    val bootstrapServers: String = "127.0.0.1:9092"
    val groupId: String = "scala-application-consumer"
    val topic: String = "topic1"


    /// Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCount").setMaster("local")

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics
    val topicsSet = topic.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
    )

    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    // Get the lines, split them into words, count the words and print
    val lines: DStream[String] = messages.map(_.value)
    val words: DStream[String] = lines.flatMap(_.split(" "))
    val wordCounts: DStream[(String, Long)] = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}

