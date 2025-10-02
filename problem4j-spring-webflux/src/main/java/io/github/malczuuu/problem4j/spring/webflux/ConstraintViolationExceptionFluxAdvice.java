package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ConstraintViolationExceptionFluxAdvice {

  private final ConstraintViolationMapping constraintViolationMapping;

  public ConstraintViolationExceptionFluxAdvice(
      ConstraintViolationMapping constraintViolationMapping) {
    this.constraintViolationMapping = constraintViolationMapping;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<Problem>> handleConstraintViolationException(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = constraintViolationMapping.map(ex, headers, status);

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
