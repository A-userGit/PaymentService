package com.innowise.paymentservice.config.feign;

import com.innowise.paymentservice.exception.decoder.FeignClientErrorDecoder;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

  private final FeignProperties properties;

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignClientErrorDecoder(properties.getName());
  }

}
