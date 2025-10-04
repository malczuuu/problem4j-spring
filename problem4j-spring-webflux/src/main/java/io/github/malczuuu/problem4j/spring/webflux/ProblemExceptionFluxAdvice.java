package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Handles {@link ProblemException} thrown by application code.
 *
 * <p>Converts the exception into a {@link Problem} response with the appropriate HTTP status and
 * content type {@code application/problem+json}.
 *
 * <p>This is intended for application-level exceptions already represented as {@link Problem}.
 */
@RestControllerAdvice
public class ProblemExceptionFluxAdvice {

  @ExceptionHandler(ProblemException.class)
  public Mono<ResponseEntity<Problem>> handleProblemException(
      ProblemException ex, ServerWebExchange exchange) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);

    Problem problem = ex.getProblem();
    if (instanceOverride != null) {
      problem = problem.toBuilder().instance(instanceOverride.toString()).build();
    }

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
