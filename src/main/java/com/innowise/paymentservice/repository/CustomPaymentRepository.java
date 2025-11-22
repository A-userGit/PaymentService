package com.innowise.paymentservice.repository;

import java.util.Date;

public interface CustomPaymentRepository{

  double getSumForPeriod(Date startPeriod, Date endPeriod);

}
