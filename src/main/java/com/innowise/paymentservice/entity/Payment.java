package com.innowise.paymentservice.entity;


import com.innowise.paymentservice.enums.PaymentStatus;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
  @Id
  private String id;
  @Field(name = "user_id")
  private long userId;
  @Field(name = "order_id")
  private long orderId;
  @Field(name = "status")
  private PaymentStatus status;
  @Field(name = "timestamp")
  private Date timestamp;
  @Field(name = "payment_amount")
  private double paymentAmount;
}
