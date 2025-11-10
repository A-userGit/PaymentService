package com.innowise.paymentservice.dto;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.enums.PaymentStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto extends CreatePaymentDto {

  private String id;
  private PaymentStatus status;
  private Date timestamp;
}
