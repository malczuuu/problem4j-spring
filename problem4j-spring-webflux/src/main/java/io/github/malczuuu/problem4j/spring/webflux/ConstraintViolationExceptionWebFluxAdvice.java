package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.internal.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
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
public class ConstraintViolationExceptionWebFluxAdvice {

  private final ConstraintViolationMapping constraintViolationMapping;

  public ConstraintViolationExceptionWebFluxAdvice(
      ConstraintViolationMapping constraintViolationMapping) {
    this.constraintViolationMapping = constraintViolationMapping;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<Problem>> handleConstraintViolationException(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem = constraintViolationMapping.map(ex, headers, status);
    if (instanceOverride != null) {
      problem = problem.toBuilder().instance(instanceOverride.toString()).build();
    }

    status = ProblemSupport.resolveStatus(problem);

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
