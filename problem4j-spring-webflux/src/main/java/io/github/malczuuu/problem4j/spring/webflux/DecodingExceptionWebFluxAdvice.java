package io.github.malczuuu.problem4j.spring.webflux;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.core.ProblemBuilder;
import io.github.malczuuu.problem4j.core.ProblemStatus;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.tracing.TracingSupport;
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

  private final List<AdviceWebFluxInspector> adviceWebFluxInspectors;

  public DecodingExceptionWebFluxAdvice(List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    this.adviceWebFluxInspectors = adviceWebFluxInspectors;
  }

  @ExceptionHandler(DecodingException.class)
  public Mono<ResponseEntity<Problem>> handleDecodingException(
      DecodingException ex, ServerWebExchange exchange) {
    ProblemContext context =
        ProblemContext.builder().traceId(exchange.getAttribute(TracingSupport.TRACE_ID)).build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    ProblemBuilder builder = Problem.builder().status(ProblemStatus.BAD_REQUEST);

    Object instanceOverride = exchange.getAttribute(TracingSupport.INSTANCE_OVERRIDE);
    if (instanceOverride != null) {
      builder = builder.instance(instanceOverride.toString());
    }

    Problem problem = builder.build();

    HttpStatus status = ProblemSupport.resolveStatus(problem);

    for (AdviceWebFluxInspector inspector : adviceWebFluxInspectors) {
      inspector.inspect(context, problem, ex, headers, status, exchange);
    }

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
