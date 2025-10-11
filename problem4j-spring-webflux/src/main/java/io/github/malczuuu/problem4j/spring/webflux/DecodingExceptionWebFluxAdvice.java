package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.util.ProblemSupport;
import java.util.List;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Handles {@link DecodingException} thrown when a message or request body cannot be decoded.
 *
 * <p>This typically occurs in WebFlux applications when the incoming data cannot be converted by
 * the configured {@code Decoder}, for example due to malformed JSON, XML, or other payloads.
 *
 * <p>The handler is responsible for returning an appropriate HTTP 400 (Bad Request) response to
 * indicate that the request body could not be decoded.
 *
 * @see org.springframework.core.codec.Decoder
 */
@RestControllerAdvice
public class DecodingExceptionWebFluxAdvice {

  private final ProblemPostProcessor problemPostProcessor;

  private final List<AdviceWebFluxInspector> adviceWebFluxInspectors;

  public DecodingExceptionWebFluxAdvice(
      ProblemPostProcessor problemPostProcessor,
      List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    this.problemPostProcessor = problemPostProcessor;
    this.adviceWebFluxInspectors = adviceWebFluxInspectors;
  }

  /**
   * Converts a {@link DecodingException} into a BAD_REQUEST {@code Problem} response and invokes
   * post-processors / inspectors.
   */
  @ExceptionHandler(DecodingException.class)
  public Mono<ResponseEntity<Problem>> handleDecodingException(
      DecodingException ex, ServerWebExchange exchange) {
    ProblemContext context =
        exchange.getAttributeOrDefault(PROBLEM_CONTEXT, ProblemContext.empty());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    Problem problem = Problem.builder().status(ProblemStatus.BAD_REQUEST).build();
    problem = problemPostProcessor.process(context, problem);

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    for (AdviceWebFluxInspector inspector : adviceWebFluxInspectors) {
      inspector.inspect(context, problem, ex, headers, status, exchange);
    }

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
