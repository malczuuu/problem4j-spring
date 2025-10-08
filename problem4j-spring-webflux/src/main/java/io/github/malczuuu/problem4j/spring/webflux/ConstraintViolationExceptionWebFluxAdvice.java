package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.resolver.ConstraintViolationResolver;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
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

  private final ConstraintViolationResolver constraintViolationResolver;
  private final List<AdviceWebFluxInspector> adviceWebFluxInspectors;

  public ConstraintViolationExceptionWebFluxAdvice(
      ConstraintViolationResolver constraintViolationResolver,
      List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    this.constraintViolationResolver = constraintViolationResolver;
    this.adviceWebFluxInspectors = adviceWebFluxInspectors;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<Problem>> handleConstraintViolationException(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    ProblemContext context =
        ProblemContext.builder().traceId(exchange.getAttribute(TracingSupport.TRACE_ID)).build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ProblemBuilder builder =
        constraintViolationResolver.resolveBuilder(context, ex, headers, status);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE);
    if (instanceOverride != null) {
      builder = builder.instance(instanceOverride.toString());
    }

    Problem problem = builder.build();

    status = ProblemSupport.resolveStatus(problem);

    for (AdviceWebFluxInspector inspector : adviceWebFluxInspectors) {
      inspector.inspect(context, problem, ex, headers, status, exchange);
    }

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
