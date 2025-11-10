package com.innowise.paymentservice.repository.impl;

import com.innowise.paymentservice.repository.CustomPaymentRepository;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements CustomPaymentRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public double getSumForPeriod(Date startPeriod, Date endPeriod) {
    MatchOperation matchOperation = Aggregation
        .match(Criteria.where("timestamp")
            .gte(startPeriod)
            .lte(endPeriod));
    GroupOperation groupOperation = Aggregation.group().sum("payment_amount").as("total");
    Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);
    AggregationResults<SumResult> results = mongoTemplate.aggregate(aggregation, "payments", SumResult.class);
    if (results.getUniqueMappedResult() != null) {
      return results.getUniqueMappedResult().getTotal();
    }
    return 0;
  }

  @Getter
  @Setter
  public static class SumResult {
    private Double total;
  }
}
