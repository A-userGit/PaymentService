package com.shop.paymentservice.config.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mongo")
public class MongoCustomProperties {

  private String clientUri;
  private String connectionUri;
  private String password;
  private String username;
}
