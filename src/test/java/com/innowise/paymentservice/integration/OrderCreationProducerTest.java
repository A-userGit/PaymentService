package com.innowise.paymentservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.external.dto.kafka.PaymentResult;
import com.innowise.paymentservice.config.kafka.KafkaTopicProperties;
import com.innowise.paymentservice.enums.PaymentStatus;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.security.test.context.support.WithMockUser;

@AutoConfigureMockMvc
public class OrderCreationProducerTest  extends BaseIntegrationTest {

  @Autowired
  WireMockServer server;

  @Autowired
  KafkaTopicProperties kafkaTopicProperties;

  private KafkaTemplate<String, CreatePaymentDto> kafkaTemplate = getKafkaTemplate();
  private KafkaConsumer<String, PaymentResult> kafkaConsumer = getKafkaConsumer();


  @Test
  @WithMockUser
  @DisplayName("Integrational order created mvc test")
  public void createPaymentFailTest() {
    try {
      server.start();
      setupMockFeign(3);
      CreatePaymentDto paymentDto = new CreatePaymentDto(6,7, 500);
      kafkaConsumer.subscribe(List.of(kafkaTopicProperties.getPaymentResultTopic()));
      kafkaTemplate.send(kafkaTopicProperties.getOrderCreatedTopic(),paymentDto);
      ConsumerRecords<String, PaymentResult> poll = kafkaConsumer.poll(Duration.ofSeconds(60));
      PaymentResult value = poll.iterator().next().value();
      assertEquals(PaymentStatus.REJECTED, value.getResult());
      setupMockFeign(6);
      CreatePaymentDto paymentDtoAccepted = new CreatePaymentDto(6, 8, 500);
      kafkaTemplate.send(kafkaTopicProperties.getOrderCreatedTopic(), paymentDtoAccepted);
      poll = kafkaConsumer.poll(Duration.ofSeconds(60));
      value = poll.iterator().next().value();
      assertEquals(PaymentStatus.APPROVED, value.getResult());
    } catch (Exception e) {
      fail("Exception during order create mvc test " + e.getMessage());
    } finally {
      if (server.isRunning()) {
        server.stop();
      }
    }
  }

  @NotNull
  private KafkaConsumer<String, PaymentResult> getKafkaConsumer() {
    Properties consumerProps = new Properties();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class.getName());
    consumerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.innowise.external.dto.kafka");
    KafkaConsumer<String, PaymentResult> consumer = new KafkaConsumer<>(consumerProps);
    return consumer;
  }

  private void setupMockFeign(int statusValue) throws JsonProcessingException {
    int[] values = new int[1];
    values[0] = statusValue;
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String userJSON = ow.writeValueAsString(values);
    server.stubFor(WireMock.get(WireMock.urlPathMatching(".*"))
        .willReturn(WireMock.aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody(userJSON)));
  }
}
