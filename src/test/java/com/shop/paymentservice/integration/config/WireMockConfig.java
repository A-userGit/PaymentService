package com.shop.paymentservice.integration.config;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class WireMockConfig {

  @Bean
  public WireMockServer mockPaymentService() {
    return new WireMockServer(options().port(8880));
  }
}
