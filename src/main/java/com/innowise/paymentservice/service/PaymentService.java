package com.innowise.paymentservice.service;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.dto.PaymentDto;
import com.innowise.paymentservice.enums.PaymentStatus;
import java.util.Date;
import java.util.List;

public interface PaymentService {

  PaymentDto createPayment(CreatePaymentDto createPaymentDto);
  List<PaymentDto> getPaymentsForOrder(long orderId);
  List<PaymentDto> getPaymentsForUser(long userId);
  List<PaymentDto> getPaymentsByStatuses(List<PaymentStatus> statuses);
  double getSumPaymentsInPeriod(Date periodStart, Date periodEnd);

}
