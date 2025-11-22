package com.innowise.paymentservice.mapper;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.dto.PaymentDto;
import com.innowise.paymentservice.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

  @Mapping(target = "timestamp", source = "timestamp", dateFormat = "dd-MM-yyyy HH:mm:ss")
  PaymentDto toPaymentDto(Payment source);

  @Mapping(target = "status", expression = "java(com.innowise.paymentservice.enums.PaymentStatus.PENDING)")
  @Mapping(target = "timestamp", expression = "java(new java.util.Date())")
  @Mapping(target = "id", ignore = true)
  Payment toPayment(CreatePaymentDto source);
}
