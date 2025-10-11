package io.github.malczuuu.problem4j.spring.webmvc;

import static io.github.malczuuu.problem4j.spring.web.context.ContextSupport.PROBLEM_CONTEXT;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

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
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ConstraintViolationExceptionMvcAdvice {

  private final ConstraintViolationResolver constraintViolationResolver;
  private final ProblemPostProcessor problemPostProcessor;

  private final List<AdviceMvcInspector> adviceMvcInspectors;

  public ConstraintViolationExceptionMvcAdvice(
      ConstraintViolationResolver constraintViolationResolver,
      ProblemPostProcessor problemPostProcessor,
      List<AdviceMvcInspector> adviceMvcInspectors) {
    this.constraintViolationResolver = constraintViolationResolver;
    this.problemPostProcessor = problemPostProcessor;
    this.adviceMvcInspectors = adviceMvcInspectors;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    ProblemContext context = (ProblemContext) request.getAttribute(PROBLEM_CONTEXT, SCOPE_REQUEST);
    if (context == null) {
      context = ProblemContext.empty();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    HttpStatus status = HttpStatus.BAD_REQUEST;

    Problem problem =
        constraintViolationResolver.resolveBuilder(context, ex, headers, status).build();
    problem = problemPostProcessor.process(context, problem);

    status = ProblemSupport.resolveStatus(problem);

    for (AdviceMvcInspector inspector : adviceMvcInspectors) {
      inspector.inspect(context, problem, ex, headers, status, request);
    }

    return new ResponseEntity<>(problem, headers, status);
  }
}
