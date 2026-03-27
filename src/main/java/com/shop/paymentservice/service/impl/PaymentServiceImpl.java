package com.shop.paymentservice.service.impl;

import com.shop.external.dto.kafka.CreatePaymentDto;
import com.shop.paymentservice.dto.PaymentDto;
import com.shop.paymentservice.entity.Payment;
import com.shop.paymentservice.enums.PaymentStatus;
import com.shop.paymentservice.exception.FeignClientException;
import com.shop.paymentservice.feignClient.PaymentProviderFeignClient;
import com.shop.paymentservice.mapper.PaymentMapper;
import com.shop.paymentservice.repository.PaymentRepository;
import com.shop.paymentservice.service.PaymentService;
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

  @Override
  public PaymentDto createPayment(CreatePaymentDto createPaymentDto) {
    Payment payment = paymentMapper.toPayment(createPaymentDto);
    ResponseEntity<int[]> providerAnswer = paymentProviderFeignClient.providePayment();
    if (providerAnswer.getBody() == null || providerAnswer.getBody().length != 1) {
      throw FeignClientException.getGeneralException(paymentProviderFeignClient.toString(),
          "UNKNOWN",
          HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
    if (providerAnswer.getBody()[0]%2 == 1) {
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
