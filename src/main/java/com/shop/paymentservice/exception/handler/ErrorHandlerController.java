package com.shop.paymentservice.exception.handler;

import com.shop.paymentservice.exception.FeignClientException;
import com.shop.paymentservice.exception.KafkaClientException;
import com.shop.paymentservice.exception.ObjectNotFoundException;
import com.mongodb.MongoCommandException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

@RestControllerAdvice
public class ErrorHandlerController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(final
  MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolationException(
      final ConstraintViolationException e) {
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
      errors.put(constraintViolation.getRootBeanClass().getSimpleName() + "."
          + constraintViolation.getPropertyPath(), e.getMessage());
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(ObjectNotFoundException.class)
  public ResponseEntity<String> handleObjectNotFoundException(final ObjectNotFoundException e) {
    return ResponseEntity.notFound().header("error", e.getMessage()).build();
  }

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<String> handleFeignClientException(final FeignClientException e) {
    return ResponseEntity.internalServerError().body(e.getMessage());
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<String> handleEmptyResultDataAccessException(
      final EmptyResultDataAccessException e) {
    return ResponseEntity.unprocessableEntity().body(e.getMessage());
  }

  @ExceptionHandler(Forbidden.class)
  public ResponseEntity<String> handleForbiddenException(
      final EmptyResultDataAccessException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Unauthorized.class)
  public ResponseEntity<String> handleUnauthorizedException(
      final EmptyResultDataAccessException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(MongoCommandException.class)
  public ResponseEntity<String> handleMongoCommandException(final  MongoCommandException e) {
    return ResponseEntity.internalServerError().body(e.getMessage());
  }

  @ExceptionHandler(KafkaClientException.class)
  public ResponseEntity<String> handleKafkaClientException(final KafkaClientException e) {
    return ResponseEntity.internalServerError().body(e.getMessage());
  }
}
