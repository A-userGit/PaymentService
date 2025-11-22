package com.innowise.paymentservice.service.impl;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.dto.PaymentDto;
import com.innowise.paymentservice.entity.Payment;
import com.innowise.paymentservice.enums.PaymentStatus;
import com.innowise.paymentservice.exception.FeignClientException;
import com.innowise.paymentservice.feignClient.PaymentProviderFeignClient;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.PaymentService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentProviderFeignClient paymentProviderFeignClient;
  private final PaymentMapper paymentMapper;
  private final static int ACCEPT_BORDER = 6;

  @Override
  public PaymentDto createPayment(CreatePaymentDto createPaymentDto) {
    Payment payment = paymentMapper.toPayment(createPaymentDto);
    ResponseEntity<int[]> providerAnswer = paymentProviderFeignClient.providePayment();
    if (providerAnswer.getBody() == null || providerAnswer.getBody().length != 1) {
      throw FeignClientException.getGeneralException(paymentProviderFeignClient.toString(),
          "UNKNOWN",
          HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
    if (providerAnswer.getBody()[0] < ACCEPT_BORDER) {
      payment.setStatus(PaymentStatus.REJECTED);
    } else {
      payment.setStatus(PaymentStatus.APPROVED);
    }
    Payment saved = paymentRepository.save(payment);
    return paymentMapper.toPaymentDto(saved);
  }

  @Override
  public List<PaymentDto> getPaymentsForOrder(long orderId) {
    return paymentRepository.getByOrderId(orderId).stream()
        .map(paymentMapper::toPaymentDto).toList();
  }

  @Override
  public List<PaymentDto> getPaymentsForUser(long userId) {
    return paymentRepository.getByUserId(userId).stream()
        .map(paymentMapper::toPaymentDto).toList();
  }

  @Override
  public List<PaymentDto> getPaymentsByStatuses(List<PaymentStatus> statuses) {
    return paymentRepository.getByStatuses(statuses).stream()
        .map(paymentMapper::toPaymentDto).toList();
  }

  @Override
  public double getSumPaymentsInPeriod(Date periodStart, Date periodEnd) {
    return paymentRepository.getSumForPeriod(periodStart, periodEnd);
  }
}
