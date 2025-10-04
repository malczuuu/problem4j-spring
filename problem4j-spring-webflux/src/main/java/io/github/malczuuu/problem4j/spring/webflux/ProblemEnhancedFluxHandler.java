package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.util.InstanceSupport.overrideInstance;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ExceptionMappingStore;
import io.github.malczuuu.problem4j.spring.web.util.StaticProblemContext;
import io.github.malczuuu.problem4j.spring.web.util.TracingSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Handles Spring framework exceptions using registered {@link
 * io.github.malczuuu.problem4j.spring.web.mapping.ExceptionMapping}s.
 *
 * <p>This class extends {@link ResponseEntityExceptionHandler} and overrides {@link
 * #handleExceptionInternal} to replace the response body with a {@link Problem} object.
 *
 * <p>Behavior:
 *
 * <ul>
 *   <li>Delegates exception-to-problem mapping to {@link ExceptionMappingStore}.
 *   <li>Sets content type to {@code application/problem+json}.
 *   <li>Falls back to {@link ProblemStatus#INTERNAL_SERVER_ERROR} if mapping fails.
 * </ul>
 */
@RestControllerAdvice
public class ProblemEnhancedFluxHandler extends ResponseEntityExceptionHandler {

  private final ExceptionMappingStore exceptionMappingStore;

  private final String instanceOverride;

  public ProblemEnhancedFluxHandler(
      ExceptionMappingStore exceptionMappingStore, String instanceOverride) {
    this.exceptionMappingStore = exceptionMappingStore;
    this.instanceOverride = instanceOverride;
  }

  @Override
  protected Mono<ResponseEntity<Object>> handleExceptionInternal(
      Exception ex,
      Object body,
      HttpHeaders headers,
      HttpStatusCode status,
      ServerWebExchange exchange) {
    StaticProblemContext context =
        new StaticProblemContext(exchange.getAttribute(TracingSupport.TRACE_ID_ATTR));

    headers = headers != null ? new HttpHeaders(headers) : new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemBuilder builder = overrideBody(ex, headers, status).toBuilder();
    builder = overrideInstance(builder, instanceOverride, context);
    Problem problem = builder.build();

    status = HttpStatus.valueOf(problem.getStatus());

    return super.handleExceptionInternal(ex, problem, headers, status, exchange);
  }

  private Problem overrideBody(Exception ex, HttpHeaders headers, HttpStatusCode status) {
    try {
      return exceptionMappingStore
          .findMapping(ex.getClass())
          .map(mapping -> mapping.map(ex, headers, status))
          .orElseGet(() -> fallbackProblem(status));
    } catch (Exception e) {
      return fallbackProblem(status);
    }
  }

  private Problem fallbackProblem(HttpStatusCode status) {
    return Problem.builder()
        .status(ProblemStatus.findValue(status.value()).orElse(ProblemStatus.INTERNAL_SERVER_ERROR))
        .build();
  }
}
