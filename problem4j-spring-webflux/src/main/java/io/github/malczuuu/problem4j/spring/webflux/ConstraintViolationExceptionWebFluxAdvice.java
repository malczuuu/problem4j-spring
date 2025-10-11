package io.github.malczuuu.problem4j.spring.webflux;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;

import io.github.malczuuu.problem4j.core.Problem;
import io.github.malczuuu.problem4j.spring.web.context.ProblemContext;
import io.github.malczuuu.problem4j.spring.web.processor.ProblemPostProcessor;
import io.github.malczuuu.problem4j.spring.web.resolver.ConstraintViolationResolver;
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
  private final ProblemPostProcessor problemPostProcessor;

  private final List<AdviceWebFluxInspector> adviceWebFluxInspectors;

  public ConstraintViolationExceptionWebFluxAdvice(
      ConstraintViolationResolver constraintViolationResolver,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceWebFluxInspector> adviceWebFluxInspectors) {
    this.constraintViolationResolver = constraintViolationResolver;
    this.problemPostProcessor = problemPostProcessor;
    this.adviceWebFluxInspectors = adviceWebFluxInspectors;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<Problem>> handleConstraintViolationException(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    ProblemContext context =
        exchange.getAttributeOrDefault(PROBLEM_CONTEXT, ProblemContext.empty());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem =
        constraintViolationResolver.resolveBuilder(context, ex, headers, status).build();
    problem = problemPostProcessor.process(context, problem);

    status = ProblemSupport.resolveStatus(problem);

    for (AdviceWebFluxInspector inspector : adviceWebFluxInspectors) {
      inspector.inspect(context, problem, ex, headers, status, exchange);
    }

    return Mono.just(new ResponseEntity<>(problem, headers, status));
  }
}
