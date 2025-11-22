package com.innowise.paymentservice.repository;

import com.innowise.paymentservice.entity.Payment;
import com.innowise.paymentservice.enums.PaymentStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, Long>, CustomPaymentRepository{

  @Query("{ 'status' : { $in : ?0 }}")
  List<Payment> getByStatuses(List<PaymentStatus> statuses);

  @Query("{ 'user_id' : ?0 }")
  List<Payment> getByUserId(long userId);

  @Query("{ 'order_id' : ?0 }")
  List<Payment> getByOrderId(long orderId);

}
