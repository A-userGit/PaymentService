package com.shop.paymentservice.config.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@RequiredArgsConstructor
public class MongoClientConfiguration {

  private final MongoCustomProperties properties;

  @Bean
  public MongoClient mongoClient() {
    return MongoClients.create(properties.getClientUri());
  }

  @Bean
  public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
    return new SimpleMongoClientDatabaseFactory(mongoClient, "payment_service");
  }

  @Bean
  public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
    return new MongoTemplate(mongoDatabaseFactory);
  }



}
