package com.shop.paymentservice.service;

import com.shop.external.dto.kafka.CreatePaymentDto;
import com.shop.paymentservice.dto.PaymentDto;
import com.shop.paymentservice.enums.PaymentStatus;
import java.util.Date;
import java.util.List;

public interface PaymentService {

  PaymentDto createPayment(CreatePaymentDto createPaymentDto);
  List<PaymentDto> getPaymentsForOrder(long orderId);
  List<PaymentDto> getPaymentsForUser(long userId);
  List<PaymentDto> getPaymentsByStatuses(List<PaymentStatus> statuses);
  double getSumPaymentsInPeriod(Date periodStart, Date periodEnd);

}
