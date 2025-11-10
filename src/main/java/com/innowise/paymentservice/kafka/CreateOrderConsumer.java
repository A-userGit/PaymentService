package com.innowise.paymentservice.kafka;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.config.kafka.KafkaTopicProperties;
import com.innowise.paymentservice.dto.PaymentDto;
import com.innowise.external.dto.kafka.PaymentResult;
import com.innowise.paymentservice.exception.KafkaClientException;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOrderConsumer {

  private final static String CONSUMER_GROUP = "group-1";

  private final PaymentService paymentService;
  private final PaymentResultProducer paymentResultProducer;
  private final KafkaTopicProperties topicProperties;

  @KafkaListener(topics = "${kafka.topic.order-created-topic}", groupId = CONSUMER_GROUP)
  public void listenOrderCreation(CreatePaymentDto message) {
    PaymentDto payment = paymentService.createPayment(message);
    PaymentResult result = new PaymentResult(payment.getOrderId(), payment.getStatus());
    try {
      paymentResultProducer.sendMessage(result);
    }catch (Exception e){
      throw KafkaClientException.producerError(topicProperties.getPaymentResultTopic(), e);
    }
  }

}
