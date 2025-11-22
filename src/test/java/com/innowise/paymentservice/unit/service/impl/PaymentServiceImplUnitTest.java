package com.innowise.paymentservice.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowise.external.dto.kafka.CreatePaymentDto;
import com.innowise.paymentservice.feignClient.PaymentProviderFeignClient;
import com.innowise.paymentservice.entity.Payment;
import com.innowise.paymentservice.enums.PaymentStatus;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.impl.PaymentServiceImpl;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplUnitTest {

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private PaymentMapper mapper;

  @Mock
  private PaymentProviderFeignClient client;

  @InjectMocks
  private PaymentServiceImpl paymentService;



  @Test
  @DisplayName("Test add rejected payment")
  public void createRejectedPaymentTest() {
    int[] externalAnswer = new int[]{3};
    Payment payment = new Payment();
    Payment spiedPayment = Mockito.spy(payment);
    when(mapper.toPayment(any())).thenReturn(spiedPayment);
    when(paymentRepository.save(any())).thenReturn(spiedPayment);
    when(client.providePayment()).thenReturn(new ResponseEntity<>(externalAnswer, HttpStatus.OK));
    paymentService.createPayment(new CreatePaymentDto(1, 1, 1));
    verify(mapper, times(1)).toPaymentDto(any());
    assertEquals(PaymentStatus.REJECTED, spiedPayment.getStatus());
  }

  @Test
  @DisplayName("Test add Accepted payment")
  public void createPaymentAcceptedTest() {
    int[] externalAnswer = new int[]{8};
    Payment payment = new Payment();
    Payment spiedPayment = Mockito.spy(payment);
    when(mapper.toPayment(any())).thenReturn(spiedPayment);
    when(paymentRepository.save(any())).thenReturn(spiedPayment);
    when(client.providePayment()).thenReturn(new ResponseEntity<>(externalAnswer, HttpStatus.OK));
    paymentService.createPayment(new CreatePaymentDto(1, 1, 1));
    verify(mapper, times(1)).toPaymentDto(any());
    assertEquals(PaymentStatus.APPROVED, spiedPayment.getStatus());
  }

  @Test
  @DisplayName("Test get payments by userId")
  public void getPaymentForUserTest() {
    when(paymentRepository.getByUserId(any(Long.class))).thenReturn(
        List.of(new Payment(), new Payment()));
    paymentService.getPaymentsForUser(1L);
    verify(mapper, times(2)).toPaymentDto(any());
  }

  @Test
  @DisplayName("Test get payments by orderId")
  public void getPaymentForOrderTest() {
    when(paymentRepository.getByOrderId(any(Long.class))).thenReturn(
        List.of(new Payment(), new Payment()));
    paymentService.getPaymentsForOrder(1L);
    verify(mapper, times(2)).toPaymentDto(any());
  }

  @Test
  @DisplayName("Test get payments by statuses")
  public void getPaymentsByStatusesTest() {
    List<PaymentStatus> statuses = List.of(PaymentStatus.PENDING, PaymentStatus.APPROVED);
    when(paymentRepository.getByStatuses(statuses)).thenReturn(
        List.of(new Payment(), new Payment()));
    paymentService.getPaymentsByStatuses(statuses);
    verify(mapper, times(2)).toPaymentDto(any(Payment.class));
  }

  @Test
  @DisplayName("Test get payments sum in period")
  public void getSumPaymentsInPeriodTest() {
    when(paymentRepository.getSumForPeriod(any(), any())).thenReturn(30.0);
    double sumPaymentsInPeriod = paymentService.getSumPaymentsInPeriod(new Date(), new Date());
    Assertions.assertEquals(30.0, sumPaymentsInPeriod);
  }

}
