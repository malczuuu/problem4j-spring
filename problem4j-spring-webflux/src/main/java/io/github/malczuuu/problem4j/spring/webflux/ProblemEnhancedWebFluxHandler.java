package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.ExceptionMappingStore;
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
public class ProblemEnhancedWebFluxHandler extends ResponseEntityExceptionHandler {

  private final ExceptionMappingStore exceptionMappingStore;

  public ProblemEnhancedWebFluxHandler(ExceptionMappingStore exceptionMappingStore) {
    this.exceptionMappingStore = exceptionMappingStore;
  }

  /**
   * <b>Note:</b> Although {@link HttpHeaders#writableHttpHeaders(HttpHeaders)} is deprecated, it is
   * used here for backward compatibility with older Spring Framework versions.
   *
   * <p>The deprecation alternative provided by Spring does not work in versions {@code 6.0.*} and
   * {@code 6.1.*} (Spring Framework versions, not Spring Boot). Therefore, this method is retained
   * to ensure compatibility across those versions.
   */
  @Override
  protected Mono<ResponseEntity<Object>> handleExceptionInternal(
      Exception ex,
      Object body,
      HttpHeaders headers,
      HttpStatusCode status,
      ServerWebExchange exchange) {
    headers = headers != null ? HttpHeaders.writableHttpHeaders(headers) : new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE_ATTR);

    Problem problem = overrideBody(ex, headers, status);
    if (instanceOverride != null) {
      problem = problem.toBuilder().instance(instanceOverride.toString()).build();
    }

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
