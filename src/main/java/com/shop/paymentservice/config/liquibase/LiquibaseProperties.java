package com.shop.paymentservice.config.liquibase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "liquibase")
public class LiquibaseProperties {
  private boolean enabled;
  private String changeLog;

}
