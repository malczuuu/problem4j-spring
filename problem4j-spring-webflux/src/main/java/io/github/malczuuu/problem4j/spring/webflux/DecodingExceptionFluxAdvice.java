package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.util.InstanceSupport.overrideInstance;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
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
public class DecodingExceptionFluxAdvice {

  private final String instanceOverride;

  public DecodingExceptionFluxAdvice(String instanceOverride) {
    this.instanceOverride = instanceOverride;
  }

  @ExceptionHandler(DecodingException.class)
  public Mono<ResponseEntity<Problem>> handleDecodingException(
      DecodingException ex, ServerWebExchange exchange) {
    ProblemContext context =
        new StaticProblemContext(exchange.getAttribute(TracingSupport.TRACE_ID_ATTR));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemBuilder builder = Problem.builder().status(ProblemStatus.BAD_REQUEST);
    builder = overrideInstance(builder, instanceOverride, context);
    Problem problem = builder.build();

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
