package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemException;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import java.util.List;
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
public class ProblemExceptionWebFluxAdvice {

  private final List<AdviceWebFluxInspector> adviceWebFluxInspectors;

  public ProblemExceptionWebFluxAdvice(List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    this.adviceWebFluxInspectors = adviceWebFluxInspectors;
  }

  @ExceptionHandler(ProblemException.class)
  public Mono<ResponseEntity<Problem>> handleProblemException(
      ProblemException ex, ServerWebExchange exchange) {
    ProblemContext context =
        ProblemContext.builder().traceId(exchange.getAttribute(TracingSupport.TRACE_ID)).build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = ex.getProblem();

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE);
    if (instanceOverride != null) {
      problem = problem.toBuilder().instance(instanceOverride.toString()).build();
    }

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    for (AdviceWebFluxInspector inspector : adviceWebFluxInspectors) {
      inspector.inspect(context, problem, ex, headers, status, exchange);
    }

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
