package com.github.atulrana.kafka.introduction

import java.time.Duration
import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.{Logger, LoggerFactory}

object KafkaClientConsumerScala {

  def main(args: Array[String]): Unit = {

    val logger: Logger = LoggerFactory.getLogger(classOf[KafkaClientConsumer].getName)

    val bootstrapServers: String = "127.0.0.1:9092"
    val groupId: String = "scala-application-consumer"
    val topic: String = "topic1"

    val kSerializer: String = classOf[StringDeserializer].getName

    logger.info("Serializer Name: " + kSerializer)
    // create consumer configs
    val properties: Properties = new Properties
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kSerializer)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kSerializer)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    // create consumer
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](properties)
    // subscribe consumer to our topic(s)
    consumer.subscribe(util.Arrays.asList(topic))
    // poll for new data
    while (true) {
      val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100)) // new in Kafka 2.0.0

      import scala.collection.JavaConversions._
      for (record <- records) {
        logger.info("Key: " + record.key + ", Value: " + record.value)
        logger.info("Partition: " + record.partition + ", Offset:" + record.offset)
      }
    }
  }
}

