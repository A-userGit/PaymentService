package com.shop.paymentservice.feignClient;

import com.shop.paymentservice.config.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${feign.payment.provider.name}", url = "${feign.payment.provider.url}",
    configuration = FeignClientConfig.class)
public interface PaymentProviderFeignClient {

  @GetMapping(value = "${feign.payment.provider.payment-endpoint}")
  ResponseEntity<int[]> providePayment();
}
