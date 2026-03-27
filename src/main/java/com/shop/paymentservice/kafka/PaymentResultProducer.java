package com.shop.paymentservice.kafka;

import com.shop.paymentservice.config.kafka.KafkaTopicProperties;
import com.shop.external.dto.kafka.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultProducer {

  private final KafkaTemplate<String, PaymentResult> kafkaTemplate;
  private final KafkaTopicProperties topicProperties;

  public void sendMessage(PaymentResult message) {
    kafkaTemplate.send(topicProperties.getPaymentResultTopic(), message);
  }
}
