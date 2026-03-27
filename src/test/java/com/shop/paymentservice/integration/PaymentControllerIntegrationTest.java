package com.shop.paymentservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.anyOf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.shop.paymentservice.enums.PaymentStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@EnableFeignClients
public class PaymentControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  @WithMockUser
  @DisplayName("Integrational payments get by user_id mvc test")
  public void getByUserIdTest() {
    try {
      mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments/user/search?userId={id}", 1)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(3)));
    } catch (Exception e) {
      fail("Exception during payment get by user_id mvc test " + e.getMessage());
    }
  }

  @Test
  @WithMockUser
  @DisplayName("Integrational payments get by order_id mvc test")
  public void getByOrderIdTest() {
    try {
      mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments/order/search?orderId={id}", 1)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    } catch (Exception e) {
      fail("Exception during order get by id mvc test " + e.getMessage());
    }
  }

  @Test
  @WithMockUser
  @DisplayName("Integrational payments get sum mvc test")
  public void getSumInPeriodTest() {
    try {
      String start = LocalDate.of(2025, 11, 10).format(DateTimeFormatter.ISO_DATE);
      String end =  LocalDate.of(2025, 11, 12).format(DateTimeFormatter.ISO_DATE);
      mockMvc.perform(
              MockMvcRequestBuilders.get("/api/v1/payments/period/sum")
                  .param("startDate", start)
                  .param("endDate", end)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", is(900.0)));
    } catch (Exception e) {
      fail("Exception during order get by id mvc test " + e.getMessage());
    }
  }

  @Test
  @WithMockUser
  @DisplayName("Integrational payments get by statuses mvc test")
  public void getByStatusesTest() {
    try {
      List<PaymentStatus> statuses = List.of(PaymentStatus.APPROVED, PaymentStatus.PENDING);
      ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
      String listJSON = ow.writeValueAsString(statuses);
      mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/statuses/search")
              .with(csrf())
              .content(listJSON)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.[0].status", anyOf(is("APPROVED"), is("PENDING"))))
          .andExpect(jsonPath("$.[1].status", anyOf(is("APPROVED"), is("PENDING"))))
          .andExpect(jsonPath("$.[2].status", anyOf(is("APPROVED"), is("PENDING"))));
    } catch (Exception e) {
      fail("Exception during orders get by statuses mvc test " + e.getMessage());
    }
  }
}