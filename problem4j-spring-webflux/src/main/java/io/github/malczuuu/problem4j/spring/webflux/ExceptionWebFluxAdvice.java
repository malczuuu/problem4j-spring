package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.internal.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.internal.TracingSupport;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Fallback exception handler for uncaught {@link Exception}s in Spring REST controllers.
 *
 * <p>This class uses {@link RestControllerAdvice} to intercept any exceptions not handled by more
 * specific exception handlers. It converts them into a standardized {@link Problem} response with:
 *
 * <ul>
 *   <li>HTTP status: {@link HttpStatus#INTERNAL_SERVER_ERROR}
 *   <li>Response body: a {@link Problem} object containing the status code and reason phrase
 *   <li>Content type: {@code application/problem+json}
 * </ul>
 *
 * <p>Intended as a **generic fallback**, it ensures that unexpected exceptions still produce a
 * consistent Problem+JSON response. For more specific exception handling, use {@link
 * ProblemEnhancedWebFluxHandler}, {@link ProblemExceptionWebFluxAdvice}.
 */
@RestControllerAdvice
public class ExceptionWebFluxAdvice {

  private final ProblemMappingProcessor problemMappingProcessor;

  public ExceptionWebFluxAdvice(ProblemMappingProcessor problemMappingProcessor) {
    this.problemMappingProcessor = problemMappingProcessor;
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Problem>> handleException(Exception ex, ServerWebExchange exchange) {
    ProblemContext context =
        new StaticProblemContext(exchange.getAttribute(TracingSupport.TRACE_ID_ATTR));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);

    Problem problem;

    if (problemMappingProcessor.isAnnotated(ex)) {
      problem = problemMappingProcessor.toProblem(ex, context);
      if (instanceOverride != null) {
        problem = problem.toBuilder().instance(instanceOverride.toString()).build();
      }
    } else {
      ProblemBuilder builder = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
      if (instanceOverride != null) {
        builder = builder.instance(instanceOverride.toString());
      }
      problem = builder.build();
    }

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
