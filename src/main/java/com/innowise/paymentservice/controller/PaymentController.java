package com.innowise.paymentservice.controller;

import com.innowise.paymentservice.dto.PaymentDto;
import com.innowise.paymentservice.enums.PaymentStatus;
import com.innowise.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "payment_security")
@RestController
@RequestMapping("/api/v1/payments/")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("order/search")
  ResponseEntity<List<PaymentDto>> getByOrder(long orderId) {
    List<PaymentDto> payments = paymentService.getPaymentsForOrder(orderId);
    return new ResponseEntity<>(payments, HttpStatus.OK);
  }

  @GetMapping("user/search")
  ResponseEntity<List<PaymentDto>> getByUser(long userId) {
    List<PaymentDto> payments = paymentService.getPaymentsForUser(userId);
    return new ResponseEntity<>(payments, HttpStatus.OK);
  }

  @GetMapping("period/sum")
  ResponseEntity<Double> getSumInPeriod(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
    double sum = paymentService.getSumPaymentsInPeriod(startDate, endDate);
    return new ResponseEntity<>(Double.valueOf(sum), HttpStatus.OK);
  }

  @PostMapping("statuses/search")
  ResponseEntity<List<PaymentDto>> getByStatuses(@RequestBody List<PaymentStatus> statuses) {
    List<PaymentDto> payments = paymentService.getPaymentsByStatuses(statuses);
    return new ResponseEntity<>(payments, HttpStatus.OK);
  }
}
