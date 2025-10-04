package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.util.InstanceSupport.overrideInstance;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
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
 * ProblemEnhancedFluxHandler}, {@link ProblemExceptionFluxAdvice}.
 */
@RestControllerAdvice
public class ExceptionFluxAdvice {

  private final ProblemMappingProcessor problemMappingProcessor;

  private final String instanceOverride;

  public ExceptionFluxAdvice(
      ProblemMappingProcessor problemMappingProcessor, String instanceOverride) {
    this.problemMappingProcessor = problemMappingProcessor;
    this.instanceOverride = instanceOverride;
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Problem>> handleException(Exception ex, ServerWebExchange exchange) {
    ProblemContext context =
        new StaticProblemContext(exchange.getAttribute(TracingSupport.TRACE_ID_ATTR));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem;

    if (problemMappingProcessor.isAnnotated(ex)) {
      problem = problemMappingProcessor.toProblem(ex, context);
      problem = overrideInstance(problem, instanceOverride, context);
    } else {
      ProblemBuilder builder = Problem.builder().status(ProblemStatus.INTERNAL_SERVER_ERROR);
      builder = overrideInstance(builder, instanceOverride, context);
      problem = builder.build();
    }

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
