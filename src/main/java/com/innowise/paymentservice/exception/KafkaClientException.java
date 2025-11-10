package com.innowise.paymentservice.exception;

public class KafkaClientException extends RuntimeException{

  public KafkaClientException(String message) {
    super(message);
  }

  public static KafkaClientException consumerError(String consumerTopic, Exception cause) {
    String message = String.format("Kafka consumer error on topic %s caused by %s", consumerTopic, cause);
    return new KafkaClientException(message);
  }

  public static KafkaClientException producerError(String producerTopic, Exception cause) {
    String message = String.format("Kafka producer error on topic %s caused by %s", producerTopic, cause);
    return new KafkaClientException(message);
  }

}
