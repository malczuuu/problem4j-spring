package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.util.InstanceSupport.overrideInstance;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.mapping.ConstraintViolationMapping;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
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

  private final String instanceOverride;

  public ConstraintViolationExceptionFluxAdvice(
      ConstraintViolationMapping constraintViolationMapping, String instanceOverride) {
    this.constraintViolationMapping = constraintViolationMapping;
    this.instanceOverride = instanceOverride;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<Problem>> handleConstraintViolationException(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    StaticProblemContext context =
        new StaticProblemContext(exchange.getAttribute(TracingSupport.TRACE_ID_ATTR));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem = constraintViolationMapping.map(ex, headers, status);
    problem = overrideInstance(problem, instanceOverride, context);

    status = HttpStatus.valueOf(problem.getStatus());

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
