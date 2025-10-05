package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.internal.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class DecodingExceptionWebFluxAdvice {

  @ExceptionHandler(DecodingException.class)
  public Mono<ResponseEntity<Problem>> handleDecodingException(
      DecodingException ex, ServerWebExchange exchange) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);

    ProblemBuilder builder = Problem.builder().status(ProblemStatus.BAD_REQUEST);
    if (instanceOverride != null) {
      builder = builder.instance(instanceOverride.toString());
    }
    Problem problem = builder.build();

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
