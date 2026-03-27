package com.shop.paymentservice.repository;

import java.util.Date;

public interface CustomPaymentRepository{

  double getSumForPeriod(Date startPeriod, Date endPeriod);

}
