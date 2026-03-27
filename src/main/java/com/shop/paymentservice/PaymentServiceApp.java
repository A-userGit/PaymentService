package com.shop.paymentservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableMongoRepositories
@EnableConfigurationProperties
@OpenAPIDefinition(servers = {@Server(url = "${swagger-server}")},
    info = @Info(title = "Payment service API",
        version = "1.0",
        description = "Payment service")
)
@SecurityScheme(name = "payment_security",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter the token without the `Bearer: ` prefix, e.g. abcde12345"
)
public class PaymentServiceApp {

  public static void main(String[] args) {
    SpringApplication.run(PaymentServiceApp.class, args);

  }

}
