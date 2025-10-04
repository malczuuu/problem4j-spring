package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.ProblemSupport;
import io.github.malczuuu.problem4j.spring.web.annotation.ProblemMappingProcessor;
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

  public ExceptionFluxAdvice(ProblemMappingProcessor problemMappingProcessor) {
    this.problemMappingProcessor = problemMappingProcessor;
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Problem>> handleException(Exception ex, ServerWebExchange exchange) {
    Problem problem = ProblemSupport.INTERNAL_SERVER_ERROR;

    if (problemMappingProcessor.isAnnotated(ex)) {
      problem = problemMappingProcessor.toProblem(ex, null);
    }

    HttpStatus status = HttpStatus.valueOf(problem.getStatus());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
