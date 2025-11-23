package com.innowise.paymentservice.integration;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.PaymentServiceApp;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@SpringBootTest(classes = PaymentServiceApp.class)
public abstract class BaseIntegrationTest {

  @Container
  private static final KafkaContainer kafka = new KafkaContainer(
      DockerImageName.parse("apache/kafka")).withReuse(true);

  @Container
  private static final GenericContainer<?> mongo = new GenericContainer<>(
      DockerImageName.parse("mongo:latest")).withExposedPorts(27017).withReuse(true);
  private static final MockOAuth2Server server;

  static {
    InetAddress authHost = null;
    try {
      authHost = InetAddress.getByName("auth-service");
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    kafka.start();
    mongo.withEnv("MONGO_INITDB_ROOT_USERNAME", "root")
        .withEnv("MONGO_INITDB_ROOT_PASSWORD", "root")
        .withEnv("MONGO_INITDB_DATABASE", "admin");
    mongo.withCopyToContainer(MountableFile.forClasspathResource("data/init-mongo.js"),
        "/docker-entrypoint-initdb.d/init-mongo.js");
    mongo.start();
    server = new MockOAuth2Server();
    server.start(authHost, 8082);
  }

  protected String kafkaBootstrap = kafka.getBootstrapServers();

  @DynamicPropertySource
  static void registerDBProperties(DynamicPropertyRegistry propertyRegistry) {
    propertyRegistry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    propertyRegistry.add("spring.kafka.consumer.properties.spring.json.trusted.packages",
        () -> "com.innowise.external.dto.kafka");
    propertyRegistry.add("mongo.client-uri",
        () -> String.format("mongodb://admin:admin@%s:%s/payment_service?authSource=admin",
            mongo.getHost(), mongo.getFirstMappedPort()));
    propertyRegistry.add("mongo.connection-uri",
        () -> String.format(
            "mongodb://%s:%s/payment_service?authSource=admin", mongo.getHost(),
            mongo.getFirstMappedPort()));
    propertyRegistry.add("feign.payment.provider.url", () -> "localhost:8880");
    propertyRegistry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
        () -> server.issuerUrl(".well-known/openid-configuration").toString());
    propertyRegistry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
        () -> "http://auth-service:8082/oauth2/jwks");
  }

  public ProducerFactory<String, CreatePaymentDto> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafka.getBootstrapServers());
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }


  public KafkaTemplate<String, CreatePaymentDto> getKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
